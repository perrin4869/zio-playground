libraryDependencies += "dev.zio" %% "zio" % "2.1.19"
libraryDependencies += "dev.zio" %% "zio-streams" % "2.1.19"
libraryDependencies += "dev.zio" %% "zio-concurrent" % "2.1.19"

// helps closing processes that do not terminate
run / fork := true
