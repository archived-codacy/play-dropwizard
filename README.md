[![Codacy Badge](https://api.codacy.com/project/badge/grade/beef0c99aa7b4579a8b3389916358232)](https://www.codacy.com/app/Codacy/play-dropwizard)
[![Circle CI](https://circleci.com/gh/codacy/play-dropwizard.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/play-dropwizard)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-dropwizard_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-dropwizard_2.11)

# play-dropwizard
dropwizard-metrics scala bindings and play components

### Prerequisites
[dropwizard-metrics](http://dropwizard.github.io/metrics/3.1.0/) works for [scala](http://www.scala-lang.org/) 2.11 and [play](https://www.playframework.com/) 2.4 

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

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

 - Identify new Static Analysis issues
 - Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
 - Auto-comments on Commits and Pull Requests
 - Integrations with Slack, HipChat, Jira, YouTrack
 - Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
