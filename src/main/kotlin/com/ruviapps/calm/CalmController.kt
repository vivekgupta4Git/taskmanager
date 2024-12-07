package com.ruviapps.calm

import com.ruviapps.calm.system.CalmCrudController
import com.ruviapps.calm.system.ModuleName

abstract class CalmController<T : CalmModel>(
    service: CalmService<T>,
    modelName: String,
    makePluralize: Boolean = true,
    authenticateRoute: Boolean = true,
) : CalmCrudController<T, T, T>(
    moduleName = ModuleName(modelName,makePluralize),
    authenticateRoute = authenticateRoute,
    service = service
)