package com.ruviapps.calm

import com.ruviapps.calm.system.CalmCrudController
import com.ruviapps.calm.system.ModuleName

abstract class CalmController<T : CalmModel>(
    modelName: String,
    makePluralize: Boolean,
    authenticateRoute: Boolean,
    service: CalmService<T>
) : CalmCrudController<T, T, T>(
    moduleName = ModuleName(modelName, makePluralize),
    authenticateRoute = authenticateRoute,
    service = service
)