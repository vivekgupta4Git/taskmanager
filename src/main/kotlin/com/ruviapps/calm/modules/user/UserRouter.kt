package com.ruviapps.calm.modules.user

import com.ruviapps.calm.CalmRouter

class UserRouter(
    basePath : String,
    controller: UserController
): CalmRouter<User>(controller = controller, basePath = basePath, groupName = "User Api"){

}