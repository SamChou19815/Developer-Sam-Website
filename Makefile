all:
	cd frontend; npm run build

build_frontend:
	cd frontend; npm run build

deploy_frontend:
	cd frontend; npm run build
	gsutil -m rsync -d -r build/frontend gs://developersam.com
	gsutil -m acl ch -r -u AllUsers:R gs://developersam.com/*

build_backend_as_container:
	./gradlew appengineStage
	cd build/staged-app; \
	gcloud beta app gen-config --custom; \
	gcloud config set project dev-sam; \
	gcloud container builds submit --tag gcr.io/dev-sam/backend-container .
