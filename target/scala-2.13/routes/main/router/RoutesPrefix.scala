// @GENERATOR:play-routes-compiler
// @SOURCE:C:/java/it pro/ITSD-DT2022-Template/conf/routes
// @DATE:Mon Feb 14 16:08:58 GMT 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
