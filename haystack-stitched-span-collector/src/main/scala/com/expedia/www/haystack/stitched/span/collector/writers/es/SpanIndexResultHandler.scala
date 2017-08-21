/*
 *  Copyright 2017 Expedia, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.expedia.www.haystack.stitched.span.collector.writers.es

import com.codahale.metrics.{Meter, Timer}
import com.expedia.www.haystack.stitched.span.collector.metrics.{AppMetricNames, MetricsSupport}
import io.searchbox.client.JestResultHandler
import io.searchbox.core.BulkResult
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.concurrent.Promise

object SpanIndexResultHandler extends MetricsSupport {
  // elastic search reports with conflict response code if a document exists with same index, type and id
  // @see See [[https://www.elastic.co/guide/en/elasticsearch/guide/current/create-doc.html]] for more details
  protected val ERROR_CODE_DUPLICATE_RECORD = 409

  protected val LOGGER: Logger = LoggerFactory.getLogger(SpanIndexResultHandler.getClass)
  protected val esDuplicateDocumentMeter: Meter = metricRegistry.meter(AppMetricNames.ES_WRITE_DUPLICATES)
  protected val esWriteFailureMeter: Meter = metricRegistry.meter(AppMetricNames.ES_WRITE_FAILURE)
}

class SpanIndexResultHandler(promise: Promise[Boolean],
                             timer: Timer.Context) extends JestResultHandler[BulkResult] {

  import SpanIndexResultHandler._

  /**
    * this callback is invoked when the elastic search writes is completed with success or warnings
    * @param result bulk result
    */
  def completed(result: BulkResult): Unit = {
    timer.stop()
    readBulkResult(result)
    // mark the promise as success even for partial failures.
    // partial failures may happen for duplicate records and is not a fatal error
    promise.success(true)
  }

  /**
    * this callback is invoked when the writes to elastic search fail completely
    * @param ex the exception contains the reason of failure
    */
  def failed(ex: Exception): Unit = {
    LOGGER.error("Fail to write all the documents in elastic search with reason:", ex)
    timer.stop()
    esWriteFailureMeter.mark()
    promise.failure(ex)
  }

  // we gather the duplicate/conflict records as a metric and a log statement.
  // this may happen only if we rewrite the batch of records due to any failure, since we rely on kafka's at least
  // once semantics, hence there may arise cases where we may end up retry writing the records that were earlier written
  // successfully.
  private def readBulkResult(result: BulkResult): Unit = {
    result.getFailedItems.foreach(item => {
      if (ERROR_CODE_DUPLICATE_RECORD == item.status) {
        esDuplicateDocumentMeter.mark()
      }
      else {
        esWriteFailureMeter.mark()
        LOGGER.error(s"Bulk operation has failed for id=${item.id}, status=${item.status} and error=${item.error}")
      }
    })
  }
}