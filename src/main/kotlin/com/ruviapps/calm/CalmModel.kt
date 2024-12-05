package com.ruviapps.calm

import com.ruviapps.calm.system.CalmGetDTO
import com.ruviapps.calm.system.CalmInsertDTO
import com.ruviapps.calm.system.CalmUpdateDTO
import kotlinx.serialization.Serializable

@Serializable
abstract class CalmModel : CalmGetDTO(), CalmInsertDTO, CalmUpdateDTO