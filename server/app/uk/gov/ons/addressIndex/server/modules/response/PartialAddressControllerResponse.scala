package uk.gov.ons.addressIndex.server.modules.response

import uk.gov.ons.addressIndex.model.server.response.address._
import uk.gov.ons.addressIndex.model.server.response.partialaddress.{AddressByPartialAddressResponse, AddressByPartialAddressResponseContainer}
import uk.gov.ons.addressIndex.server.model.dao.QueryValues

trait PartialAddressControllerResponse extends AddressResponse {

  def BadRequestPartialTemplate(queryValues: QueryValues, errors: AddressResponseError*): AddressByPartialAddressResponseContainer = {
    AddressByPartialAddressResponseContainer(
      apiVersion = apiVersion,
      dataVersion = dataVersion,
      response = ErrorPartialAddress(queryValues),
      status = BadRequestAddressResponseStatus,
      errors = errors
    )
  }

  def ShortSearch(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, ShortQueryAddressResponseError)
  }

  def LimitNotNumericPartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, LimitNotNumericAddressResponseError)
  }

  def OffsetNotNumericPartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, OffsetNotNumericAddressResponseError)
  }

  def LimitTooSmallPartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, LimitTooSmallAddressResponseError)
  }

  def OffsetTooSmallPartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, OffsetTooSmallAddressResponseError)
  }

  def LimitTooLargePartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, LimitTooLargeAddressResponseError)
  }

  def OffsetTooLargePartial(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, OffsetTooLargeAddressResponseError)
  }

  def PartialEpochInvalid(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, EpochNotAvailableError)
  }

  def EpochNotAvailable(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, EpochNotAvailableError)
  }

  def PartialFromSourceInvalid(queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    BadRequestPartialTemplate(queryValues, FromSourceInvalidError)
  }

  def FailedRequestToEsPartialAddress(detail: String, queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    val enhancedError = new AddressResponseError(FailedRequestToEsPartialAddressError.code, FailedRequestToEsPartialAddressError.message.replace("see logs", detail))
    AddressByPartialAddressResponseContainer(
      apiVersion = apiVersion,
      dataVersion = dataVersion,
      response = ErrorPartialAddress(queryValues),
      status = InternalServerErrorAddressResponseStatus,
      errors = Seq(enhancedError)
    )
  }

  def FailedRequestToEsTooBusyPartialAddress(detail: String, queryValues: QueryValues): AddressByPartialAddressResponseContainer = {
    val enhancedError = new AddressResponseError(FailedRequestToEsPartialAddressError.code, FailedRequestToEsPartialAddressError.message.replace("see logs", detail))
    AddressByPartialAddressResponseContainer(
      apiVersion = apiVersion,
      dataVersion = dataVersion,
      response = ErrorPartialAddress(queryValues),
      status = TooManyRequestsResponseStatus,
      errors = Seq(enhancedError)
    )
  }

  def ErrorPartialAddress(queryValues: QueryValues): AddressByPartialAddressResponse = {
    AddressByPartialAddressResponse(
      input = queryValues.inputOrDefault,
      addresses = Seq.empty,
      filter = queryValues.filterOrDefault,
      fallback = queryValues.fallbackOrDefault,
      historical = queryValues.historicalOrDefault,
      epoch = queryValues.epochOrDefault,
      limit = queryValues.limitOrDefault,
      offset = queryValues.offsetOrDefault,
      total = 0,
      maxScore = 0f,
      verbose = queryValues.verboseOrDefault,
      fromsource = queryValues.fromSourceOrDefault
    )
  }

}
