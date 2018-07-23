#!/usr/bin/env bash

set -x

container_name='gcr.io/dev-sam/frontend-container'
container_tag=`date +%s`
full_container_tag="${container_name}:${container_tag}"
echo "The container tag will be: ${full_container_tag}"

gcloud config set project dev-sam
gcloud container builds submit -t ${full_container_tag} .

kubectl set image deployment frontend-workload *=${full_container_tag}
