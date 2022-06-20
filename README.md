# OddHours (An android app)

## Project Setup
Clone the repo, open the project in Android Studio, build and hit "Run". Done!

## Architecture Overview
This app makes no api calls. It runs purely offline (and that was one of the main intentions) as
there is no need for network connection.

Data is stored locally using SQLite and stored on the mobile device.

The domain models are used throughout the app. They are plain Kotlin objects. On app startup, a
call is made to the database to load up the information into these models. Our job repository
takes care of functions necessary for CRUD operations.

We currently use one external library for displaying charts: android charts
- https://github.com/HackPlan/AndroidCharts
- https://camposha.info/android-examples/android-chart-libraries/#gsc.tab=0

## Version Control Workflow
We loosely use the "Git flow" approach: `main` is the release branch - it should always be releasable
and only merged into when we have tested and verified that everything works and is good to go.

Daily development (features, bugfixes and other tasks) are done as branches off of develop then
merged back into develop via pull requests. Pull requests usually need a review and have to pass
unit tests (and other pipeline tasks) before being merged unless it's super trivial (or contains)
just documentation. Pull requests are usually merged and not squashed or rebased unless necessary
(example: a messy branch with lots of trivial commits should be rebased before being merged in).

## How to release a new version
(TODO)