[![Codacy Badge](https://api.codacy.com/project/badge/grade/beef0c99aa7b4579a8b3389916358232)](https://www.codacy.com/app/Codacy/play-dropwizard)
[![Circle CI](https://circleci.com/gh/codacy/play-dropwizard.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/play-dropwizard)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-dropwizard_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-dropwizard_2.11)

# play-dropwizard
[dropwizard-metrics](http://dropwizard.github.io/metrics/3.1.0/) works for [scala](http://www.scala-lang.org/) 2.11 and [play](https://www.playframework.com/) 2.4 

### Prerequisites
the library currently works for scala 2.11 and 

### Setup
add the following as a dependency to your sbt configuration:

```scala
libraryDependencies += "com.codacy" %% "play-dropwizard" % metricVersion
```

### Usage
start by adding the following import to your code
```scala
import codacy.metrics.dropwizard._
````
and measure some code. timing for example you can do by:
```scala
val name = TimerName(myTimer)
timed(name){ block }
```


