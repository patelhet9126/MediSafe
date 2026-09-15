# MediSafe
A user-friendly Android healthcare application for managing emergency medical profiles, medical history, allergies, medications, medical reports, emergency contacts, and QR-based emergency medical access using Kotlin, XML, and SQLite.

## 📌 Project Overview

During a medical emergency or accident, important medical information such as blood group, allergies, medical history, medications, and emergency contacts may not be immediately available.

The **Smart Emergency Medical Card & Health Record Management System** is designed to solve this problem by allowing users to digitally store and manage their important medical information in one place.

The application allows users to create medical profiles, manage medical history, store medical reports, add emergency contacts, manage family profiles, and generate an Emergency Medical Card with a QR code.

---

## 🎯 Objectives

- Provide quick access to important emergency medical information.
- Digitally manage personal and family medical profiles.
- Store medical history in an organized manner.
- Manage allergies and medications.
- Store and manage medical reports.
- Provide an Emergency Medical Card with a QR code.
- Allow QR-based emergency identification.
- Reduce the difficulty of finding important medical information during emergencies.
- Provide simple CRUD operations for medical information.
- Keep the application simple and user-friendly.

---

## ✨ Features

### 👤 User Profile

- Create a personal medical profile.
- Add profile photo.
- Store basic personal information.
- Store date of birth / age.
- Store gender.
- Store blood group.
- Store organ donor information.
- Generate a unique medical profile ID.

### 🏥 Medical History

Users can manage multiple medical conditions.

Features include:

- Add new medical condition.
- View medical condition.
- Edit medical condition.
- Update medical information.
- Delete medical condition.
- Store treatment information.
- Store doctor and hospital information.
- Store surgery/treatment details.
- Add related medical reports.

### 💊 Allergies

Users can:

- Add allergies.
- View allergies.
- Edit allergies.
- Update allergy information.
- Delete allergies.

### 💉 Medications

Users can:

- Add medications.
- View medications.
- Edit medications.
- Update medications.
- Delete medications.

### 📄 Medical Report Wallet

The Report Wallet allows users to manage medical documents.

Features include:

- Upload new medical reports.
- View reports.
- Edit report information.
- Replace existing reports.
- Update report information.
- Delete reports.
- Associate reports with medical conditions.

### 🚑 Emergency Contacts

Users can manage multiple emergency contacts.

Features include:

- Add primary emergency contact.
- Add additional emergency contacts.
- Edit contacts.
- Update contacts.
- Delete contacts.
- Store relationship and phone number.

### 👨‍👩‍👧‍👦 Family Profiles

The application can support medical profiles for family members.

Each family member's medical information is maintained separately to prevent information from being mixed between profiles.

### 🪪 Emergency Medical Card

The application provides a digital Emergency Medical Card containing important emergency information.

The card can include:

- Profile photo.
- Name.
- Age.
- Gender.
- Blood group.
- Emergency information.
- Emergency contacts.
- Unique medical profile ID.
- QR code.

### 📱 QR Code

The Emergency Medical Card contains a QR code linked to the user's emergency profile reference.

The QR system is designed so that sensitive medical reports and credentials are not directly stored inside the QR code.

The QR can be scanned using the application to identify the appropriate emergency profile.

### 🔐 Security

Because the application handles sensitive medical information, security and data protection are important design considerations.

The application follows security-conscious practices such as:

- Profile-based data separation.
- No passwords stored in plain text.
- No sensitive medical reports inside QR codes.
- No sensitive information in debug logs.
- Delete confirmation before removing records.
- Private local file storage for medical documents.
- Validation of user input.
- Protection against accidental deletion.
- Controlled access to sensitive information.

---

## 🛠️ Technology Stack

### Android

- **Kotlin**
- **XML**
- **ConstraintLayout**
- **Android Studio**

### Database

- **SQLite**
- **SQLiteOpenHelper**
- Custom `DatabaseHelper.kt`

### Other

- QR Code generation
- QR Code scanning
- Android local storage
- XML-based UI

---

## 🗂️ Project Structure

```text
app/
│
├── src/
│   └── main/
│       │
│       ├── java/
│       │   └── com.example.medicalcard/
│       │       │
│       │       ├── activities/
│       │       ├── models/
│       │       ├── database/
│       │       │   └── DatabaseHelper.kt
│       │       ├── adapters/
│       │       └── utils/
│       │
│       ├── res/
│       │   ├── layout/
│       │   ├── drawable/
│       │   ├── mipmap/
│       │   └── values/
│       │
│       └── AndroidManifest.xml
│
└── README.md
