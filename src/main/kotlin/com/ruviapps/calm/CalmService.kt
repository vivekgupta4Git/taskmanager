package com.ruviapps.calm

abstract class CalmService<T : CalmModel>(
    repository: CalmRepository<T>
) : CalmCrudService<T, T, T>(repository)