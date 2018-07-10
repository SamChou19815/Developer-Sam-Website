all:
	cd frontend; npm run build

deploy_frontend:
	cd frontend; npm run build
	gsutil -m rsync -d -r build/frontend gs://developersam.com
	gsutil -m acl ch -r -u AllUsers:R gs://developersam.com/*

build_backend_as_container:
	./gradlew appengineStage
	cp backend-main/src/main/appengine/* build/staged-app
	cd build/staged-app; \
	gcloud config set project dev-sam; \
	gcloud container builds submit --tag gcr.io/dev-sam/backend-container .

update_indices:
	cd ~/.config/gcloud/emulators/datastore/WEB-INF; \
	gcloud datastore create-indexes index.yaml;\
	gcloud datastore cleanup-indexes index.yaml
