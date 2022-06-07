# Wacayang, Indonesian Wayang App

## Android APK File
Link: https://drive.google.com/file/d/17EXgCPaWJuxIVNsX-qUjfb0lgfdHY-oy/view?usp=sharing<br/>
Alternative Link: https://drive.google.com/file/d/1hXyMuBRKxxPgjiQkxqbVkO4Q_6UlPJuB/view?usp=sharing

## About
Wacayang is a mobile application that able to identify Indonesian wayang kulit characters. This app uses images uploaded by the user as input, and the information about the name, description, image, and related video about the identified wayang character will be displayed on the app.

## App Features
![Feature 1](https://github.com/Wacayang-Bangkit-2022/Wacayang-Documentation/blob/main/assets/feature%20(1).png)
![Feature 2](https://github.com/Wacayang-Bangkit-2022/Wacayang-Documentation/blob/main/assets/feature%20(2).png)
![Feature 3](https://github.com/Wacayang-Bangkit-2022/Wacayang-Documentation/blob/main/assets/feature%20(3).png)
![Feature 4](https://github.com/Wacayang-Bangkit-2022/Wacayang-Documentation/blob/main/assets/feature%20(4).png)
* Search various Indonesia wayang by typing on search bar.
* Upload image from camera or gallery to predict its wayang character using machine learning.
* Information about Indonesian wayangs inclunding name, story, images, and related wayang shows from local Indonesian puppeter.
* Personalization to add and remove wayang to/from favorite library.
* Share thoughts and knowledge through posting comments.
* Secured account sign in using Google Firebase JWT token.

## Technology
* Android Studio IDE to develop the Android application.
* Google Cloud Platform as deployment platform for REST API, database, and ML model.
* Tensorflow and Keras to develop, train, and deploy ML model.

## Integration Method
Wacayang consists of three components, Android, Cloud Computing, and Machine Learning. Basically, to integrate these, Cloud Computing acts as service to bridge communication between Android and Machine Learning. Here is a simple illustration on how our integration method works.
![Integration Method](https://github.com/Wacayang-Bangkit-2022/Wacayang-Documentation/blob/main/assets/integration.png)
Integration method explanation:
1. Android app send a network request using Retrofit library. This request has JWT token as Authorization header.
2. Cloud Run acts as service to serve request from the app. It will verify the token first before proceed.
3. After token is verified, Cloud Run services will access back-end app depending on the request. If it looking for user or wayang information, it will query to SQL database. If it looking for wayang image prediction, then it will post that image to ML model.
4. After back-end app finished processing, Cloud Run services will return the result as JSON literals to the Android app.
5. Android app will process the JSON literals and show relevant information to the user.

## Android Studio Project Installation
### Components
Wacayang Android app is developed using Android Studio IDE. Here are components that we used.
* Developed using [Kotlin](https://kotlinlang.org/) language.
* Composed by [Activity](https://developer.android.com/reference/android/app/Activity) and [Fragment](https://developer.android.com/guide/fragments).
* Using [RecycleView](https://developer.android.com/guide/topics/ui/layout/recyclerview) and its adapater for item listing.
* [CameraX](https://developer.android.com/training/camerax) to utilize mobile camera features including flash, front/back camera, and more.
* Using [Retrofit](https://square.github.io/retrofit/) library for network request.
* Using official [Youtube API](https://developers.google.com/youtube/android/player) for Youtube video player.
* [BottomNavigation](https://developer.android.com/reference/com/google/android/material/bottomnavigation/BottomNavigationView) to navigate between main menus (Home, Favorite, and Settings).
* Connected to [Firebase Auth](https://firebase.google.com/docs/auth) for Google and anonymous sign in.
* Utilize [LiveData](https://developer.android.com/topic/libraries/architecture/livedata), [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), and [Repository](https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern) pattern for Single Source of Truth (SSOT)
### Workflow
#### 1. Clone The Project and Open It in Android Studio
```
git clone https://github.com/Wacayang-Bangkit-2022/Wacayang-MobileDev.git
```
#### 2. Connect the Project to Your Firebase Auth
* Head to your [Firebase Console](https://console.firebase.google.com/).
* Then create or use your existing Firebase project.
* Activate Firebase Authentication feature.
* Open `Project Settings -> General`, select `New App`, and choose Android app.
* Fill debug SHA1 fingerprint. You can find this by execute `Gradle -> signingReport` on Android Studio.
* After the new Firebase app added, you will see your debug SHA1 added to that Firebase app. You can add another SHA1 such as your signed app SHA1. You can find your signed app SHA1 by executing this command on terminal. But, you will need to create your [Keystore](https://developer.android.com/studio/publish/app-signing#generate-key) first
```
keytool -list -v -keystore <your keystore path> -alias <your alias>
```
* Install [Firebase SDK](https://developer.android.com/studio/write/firebase) to your Android Studio project.
* Download the `google-service.json` from your Firebase Console, and copy it to the `app` folder of your Android Studio project.
#### 3. Run or Build The App
After you open the project, wait for the Gradle to finish building first. Then you can choose to build debug app by using `Run -> Run'app'`. Or you can build signed App by head to `Build -> Generate Signed Bundle/APK`.
