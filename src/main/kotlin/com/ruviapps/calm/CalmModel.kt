package com.ruviapps.calm

import kotlinx.serialization.Serializable

@Serializable
abstract class CalmModel : CalmGetDTO(), CalmInsertDTO, CalmUpdateDTO