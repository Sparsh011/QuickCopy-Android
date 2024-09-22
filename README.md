# Getting Started

## Step 1
Clone the repository on your system.

## Step 2
Open the project and run the release build (preferably for better performance).

## Step 3
You are ready to go! The app will automatically appear in the default Android sender, and you can select the app to copy the image from the URL. However, there are a few [limitations](#limitations).

## Limitations

1. **Login Requirement**: If the URL you are trying to download an image from requires the user to be logged in, the image may not be accessible or downloadable because it can't be retrieved without authentication.

2. **Incomplete Loading**: If a popup (such as a cookie consent banner or an ad) appears before the image fully loads, the image may not be downloaded. This occurs because the JavaScript function can't correctly access the image if another UI element blocks or disrupts the image loading process.
