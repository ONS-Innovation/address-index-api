package uk.gov.ons.addressIndex.model.db.index

import com.sksamuel.elastic4s.{Hit, HitReader}

import scala.util.Try

case class HybridAddress(uprn: String,
                         parentUprn: String,
                         relatives: Option[Seq[Relative]],
                         crossRefs: Option[Seq[CrossRef]],
                         postcodeIn: Option[String],
                         postcodeOut: Option[String],
                         lpi: Seq[NationalAddressGazetteerAddress],
                         paf: Seq[PostcodeAddressFileAddress],
                         nisra: Seq[NisraAddress],
                         score: Float,
                         classificationCode: String,
                         fromSource: String)

object HybridAddress {
  /**
    * An empty HybridAddress, used in bulk search to show the address that didn't yield any results
    */
  val empty = HybridAddress(
    uprn = "",
    parentUprn = "",
    relatives = Some(Seq.empty),
    crossRefs = Some(Seq.empty),
    postcodeIn = Some(""),
    postcodeOut = Some(""),
    lpi = Seq.empty,
    paf = Seq.empty,
    nisra = Seq.empty,
    score = 0,
    classificationCode = "",
    fromSource = "",
  )

  // this `implicit` is needed for the library (elastic4s) to work
  implicit object HybridAddressHitReader extends HitReader[HybridAddress] {
    /**
      * Transforms hit from Elastic Search into a Hybrid Address
      * Used for the elastic4s library
      *
      * @param hit Elastic's response
      * @return generated Hybrid Address
      */
    override def read(hit: Hit): Either[Throwable, HybridAddress] = {
      val cRefs: Seq[Map[String, AnyRef]] = Try {
        hit.sourceAsMap("crossRefs").asInstanceOf[List[Map[String, AnyRef]]].map(_.toMap)
      }.getOrElse(Seq.empty)

      val rels: Seq[Map[String, AnyRef]] = Try {
        hit.sourceAsMap("relatives").asInstanceOf[List[Map[String, AnyRef]]].map(_.toMap)
      }.getOrElse(Seq.empty)

      val lpis: Seq[Map[String, AnyRef]] = Try {
        hit.sourceAsMap("lpi").asInstanceOf[List[Map[String, AnyRef]]].map(_.toMap)
      }.getOrElse(Seq.empty)

      val pafs: Seq[Map[String, AnyRef]] = Try {
        hit.sourceAsMap("paf").asInstanceOf[List[Map[String, AnyRef]]].map(_.toMap)
      }.getOrElse(Seq.empty)

      val nisras: Seq[Map[String, AnyRef]] = Try {
        hit.sourceAsMap("nisra").asInstanceOf[List[Map[String, AnyRef]]].map(_.toMap)
      }.getOrElse(Seq.empty)

      Right(HybridAddress(
        uprn = hit.sourceAsMap("uprn").toString,
        parentUprn = hit.sourceAsMap("parentUprn").toString,
        relatives = Some(rels.map(Relative.fromEsMap).sortBy(_.level)),
        crossRefs = Some(cRefs.map(CrossRef.fromEsMap)),
        postcodeIn = Some(hit.sourceAsMap("postcodeIn").toString),
        postcodeOut = Some(hit.sourceAsMap("postcodeOut").toString),
        lpi = lpis.map(NationalAddressGazetteerAddress.fromEsMap),
        paf = pafs.map(PostcodeAddressFileAddress.fromEsMap),
        nisra = nisras.map(NisraAddress.fromEsMap),
        score = hit.score,
        classificationCode = hit.sourceAsMap("classificationCode").toString,
        fromSource = Try(hit.sourceAsMap("fromSource").toString).getOrElse("EW"),
      ))
    }
  }
}