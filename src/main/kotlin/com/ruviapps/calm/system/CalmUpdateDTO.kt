package com.ruviapps.calm.system

import org.bson.conversions.Bson

/**
 * Enforcing Update Dto to provide conversion to Bson
 * @return [Bson] a Bson Document
 */
interface CalmUpdateDTO {
    fun CalmUpdateDTO.toUpdates(): Bson
}