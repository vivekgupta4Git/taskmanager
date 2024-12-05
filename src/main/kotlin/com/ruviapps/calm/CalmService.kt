package com.ruviapps.calm

import com.ruviapps.calm.system.CalmCrudService

abstract class CalmService<T : CalmModel>(
    repository: CalmRepository<T>
) : CalmCrudService<T, T, T>(repository)

