package com.flysolo.etrike.models.directions


data class NearbyPlaces(
    val htmlAttributions: List<String?>? = null,
    val results: List<Result?>? = null,
    val status: String? = null
) {
    data class Result(
        val businessStatus: String? = null,
        val geometry: Geometry? = null,
        val icon: String? = null,
        val iconBackgroundColor: String? = null,
        val iconMaskBaseUri: String? = null,
        val name: String? = null,
        val openingHours: OpeningHours? = null,
        val photos: List<Photo?>? = null,
        val placeId: String? = null,
        val plusCode: PlusCode? = null,
        val priceLevel: Int? = null,
        val rating: Double? = null,
        val reference: String? = null,
        val scope: String? = null,
        val types: List<String?>? = null,
        val userRatingsTotal: Int? = null,
        val vicinity: String? = null
    ) {
        data class Geometry(
            val location: Location? = null,
            val viewport: Viewport? = null
        ) {
            data class Location(
                val lat: Double? = null,
                val lng: Double? = null
            )

            data class Viewport(
                val northeast: Location? = null,
                val southwest: Location? = null
            )
        }

        data class OpeningHours(
            val openNow: Boolean? = null
        )

        data class Photo(
            val height: Int? = null,
            val htmlAttributions: List<String?>? = null,
            val photoReference: String? = null,
            val width: Int? = null
        )

        data class PlusCode(
            val compoundCode: String? = null,
            val globalCode: String? = null
        )
    }
}
