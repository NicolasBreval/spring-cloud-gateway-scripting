rootProject.name = "spring-cloud-gateway-scripting-parent"

include("core")
project(":core").name = "gateway-scripting-core"

include("groovy")
project(":groovy").name = "gateway-scripting-groovy"
