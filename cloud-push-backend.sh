#!/usr/bin/env bash

set -x

# Staging
./gradlew build
mkdir -p build/staging
cp build/libs/website-5.0-all.jar build/staging
cp src/main/docker/* build/staging

# Cloud Build
container_name='gcr.io/dev-sam/backend-container'
container_tag=`date +%s`
full_container_tag="${container_name}:${container_tag}"
echo "The container tag will be: ${full_container_tag}"
cd build/staging; \
gcloud config set project dev-sam; \
gcloud config set compute/zone us-central1-a; \
gcloud container clusters get-credentials web-cluster; \
gcloud container builds submit -t ${full_container_tag} .

# Rolling Update
kubectl set image deployment backend-workload *=${full_container_tag}
