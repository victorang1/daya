
# daya - B21-CAP0034

## Table of contents
* [Introduction](#introduction)
* [Technologies](#technologies)
* [Setup](#setup)
* [What's Included](#whats-included)
* [Creators](#creators)

## Introduction

daya is a mobile-based application for facilitating users’ discovery of cultural heritages or such things. This application can be used by users from anywhere or when the user visit some places in Indonesia. With some features contained in the application, the users can get information about several places that they want to know.

## Technologies

- Kotlin
- Retrofit
- Jetpack Navigation Component
- LiveData & ViewModel
- Koin
- RxJava3 & RxBinding
- CameraX
- Firebase
- TensorFlow Lite
- AR Core (Coming soon)

## Machine Learning Structure
- Image Classification
Using mobilenet transfer learning and doing scrapping to get the data to make the model.

- Question And Answer
Using mobilebert_qa transfer learning and available data from tensorflow.org to make the model.

## Setup

To be able login with **Google Account**, you have to download this [file](https://drive.google.com/uc?export=download&id=1Bfgm_KsrQhdkwzHWEyeURxAtoTgl0Az6). After you downloaded the file, put that file inside **app** folder.
```
daya/
├── .gradle/
├── .idea/
├── app/
    ├── dayakeystore.keystore
    └── ...
└── ...
```
To run the project use the following command in the Android Studio terminal:
>gradlew installRelease

**NOTE: If you are not using Google Account, you can skip this setup section and run the project as usual.**

## What's Included

```
daya/
└── app/
    ├── MachineLearning/ImageClassification/	# Notebook Files and Dataset
    └── src/
        ├── java/ 				# App Features
        ├── assets/ 				# Model for QnA & Prediction
        │   ├── labels.txt
        │   ├── model.tflite
        │   └── qa.json
        ├── ml/                 		# Model for Image Classification
	    └── PlaceModel.tflite
        └── res/ 				# App Resources

```




## Creators

- A0050416 - Victor
- A3332986 - Fatma Satyani
- C0050409 - Jeremy Ansellino Gunawan
- C3332985 - Monica Novalensiago
- M2132068 - Ryo Kusnadi
- M2132069 - Ananda Wiradharma
