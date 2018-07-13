# Developer Sam Website

The code of my own website that is now open-sourced.

This repository contains both the frontend and the backend. The frontend is now developed with 
Angular 6 and Angular Material 6. The backend is developed by Kotlin.

Currently, the project is undergoing an improvement of structure. 

# Open Source Acknowledgement

It is impossible for this website to run without

- [Spark (Apache 2.0)](https://github.com/perwendel/spark)
- [pac4j (Apache 2.0)](https://github.com/pac4j/pac4j)
- [spark-pac4j (Apache 2.0)](https://github.com/pac4j/spark-pac4j)
- [GSON (Apache 2.0)](https://github.com/google/gson)
- [Angular 6 (MIT)](https://github.com/angular/angular)
- [Angular Material (MIT)](https://github.com/angular/material2)
- [TEN (MIT)](https://github.com/SamChou19815/TEN)
- [OKaml-Lib(MIT)](https://github.com/SamChou19815/Okaml-Lib)

# Build Commands

- Deploy Frontend: `./gradlew deployFrontend`
- Update Datastore Indices: `./gradlew updateDatastoreIndices`
- Cloud Build Backend Container: `./gradlew cloudBuildBackendContainer`
- Local Run Backend Container: `./gradlew localBuildAndRunBackendContainer`
