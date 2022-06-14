## Resources

### github actions
- tutorial: https://www.youtube.com/watch?v=K9w01h4-Wnc&ab_channel=goobar
- info:
  - https://stackoverflow.com/questions/44860306/what-is-gradle-wrapper-and-the-gradlew-bat-file
  - https://ncorti.com/blog/howto-github-actions-building-android
- validate gradle wrapper (why):
  - https://blog.gradle.org/gradle-wrapper-checksum-verification-github-action
  - https://github.com/cortinico/kotlin-android-template/blob/main/.github/workflows/gradle-wrapper-validation.yml

### potentially useful android libraries
- https://github.com/mmahmoudothman/UltimateAndroidReference

### android xml file naming convention
- https://medium.com/mindorks/android-resource-naming-convention-42e4e8026614
- case types: https://chaseadams.io/posts/most-common-programming-case-types/

### implementing ROOM Db in android
- https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0
- room is like a wrapper for SQLite and allows us to avoid having to write a lot of the boilerplate code. For future releases we can focus on perhaps implementing the backend with Room.

### charts feature and other libraries for charts
- implemented in app: https://linear.app/oddhours/issue/ODD-16/notifications-fragment
- MPAndroidCharts library: https://trello.com/c/Y6GZaIB2/46-mpandroidcharts-library
  - useful links:
    - http://developine.com/android-grouped-stacked-bar-chart-using-mpchart-kotlin/
    - https://stackoverflow.com/questions/27566916/how-to-remove-description-from-chart-in-mpandroidchart

### misc
- adding this line in Gradle Build file allows us to avoid having to call FindViewById in a fragment and instead we can directly reference the view by it's ID
`id 'kotlin-android-extensions'`
- information for making a good readme.md file: https://tknilsson.com/2018/10/19/android-friday-things-i-want-to-see-in-the-readme-file/