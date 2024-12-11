package com.ruviapps.calm

import com.ruviapps.calm.system.CalmCrudRouter

abstract class CalmRouter<T : CalmModel>(
    basePath: String,
    controller: CalmController<T>
) : CalmCrudRouter<T, T, T>(basePath, controller)