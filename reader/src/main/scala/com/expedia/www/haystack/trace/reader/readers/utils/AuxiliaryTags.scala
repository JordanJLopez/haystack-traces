package com.expedia.www.haystack.trace.reader.readers.utils

object AuxiliaryTags {
  val INFRASTRUCTURE_LOCATION = "X-HAYSTACK-AWS-REGION"
  val INFRASTRUCTURE_PROVIDER = "X-HAYSTACK-INFRASTRUCTURE-PROVIDER"
  
  val IS_MERGED_SPAN = "X-HAYSTACK-IS-MERGED-SPAN"
  val NETWORK_DELTA = "X-HAYSTACK-NETWORK-DELTA"

  val CLIENT_SERVICE_NAME = "X-HAYSTACK-CLIENT-SERVICE-NAME"
  val CLIENT_OPERATION_NAME = "X-HAYSTACK-CLIENT-OPERATION-NAME"
  val CLIENT_INFRASTRUCTURE_PROVIDER = "X-HAYSTACK-CLIENT-INFRASTRUCTURE-PROVIDER"
  val CLIENT_INFRASTRUCTURE_LOCATION = "X-HAYSTACK-CLIENT-INFRASTRUCTURE-LOCATION"
  val CLIENT_START_TIME = "X-HAYSTACK-CLIENT-START-TIME"
  val CLIENT_DURATION = "X-HAYSTACK-CLIENT-DURATION"
  
  val SERVER_SERVICE_NAME = "X-HAYSTACK-SERVER-SERVICE-NAME"
  val SERVER_OPERATION_NAME = "X-HAYSTACK-SERVER-OPERATION-NAME"
  val SERVER_INFRASTRUCTURE_PROVIDER = "X-HAYSTACK-SERVER-INFRASTRUCTURE-PROVIDER"
  val SERVER_INFRASTRUCTURE_LOCATION = "X-HAYSTACK-SERVER-INFRASTRUCTURE-LOCATION"
  val SERVER_START_TIME = "X-HAYSTACK-SERVER-START-TIME"
  val SERVER_DURATION = "X-HAYSTACK-SERVER-DURATION"
}