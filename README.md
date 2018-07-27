# Developer Sam Website

The code of my own website that is open-sourced.

This repository contains both the frontend and the backend. The frontend is now developed with 
Angular 6 and Angular Material 6. The backend is developed by Kotlin.

# Open Source Acknowledgement

It is impossible for this website to run without

- [Spark (Apache 2.0)](https://github.com/perwendel/spark)
- [pac4j (Apache 2.0)](https://github.com/pac4j/pac4j)
- [spark-pac4j (Apache 2.0)](https://github.com/pac4j/spark-pac4j)
- [gson (Apache 2.0)](https://github.com/google/gson)
- [Angular 6 (MIT)](https://github.com/angular/angular)
- [Angular Material (MIT)](https://github.com/angular/material2)
- [TypedStore (MIT)](https://github.com/SamChou19815/typed-store)
- [KineticS (MIT)](https://github.com/SamChou19815/kinetics)
- [SAMPL (MIT)](https://github.com/SamChou19815/sampl)
- [TEN (MIT)](https://github.com/SamChou19815/ten)
- [OKaml-Lib(MIT)](https://github.com/SamChou19815/okaml-lib)

# Build Commands

- Deploy Frontend: `bash frontend/cloud-push-frontend.sh`
- Deploy Backend: `bash cloud-push-backend.sh`
- Update Datastore Indices: `./gradlew updateDatastoreIndices`
- Local Run Backend Container: `./gradlew localBuildAndRunBackendContainer`
