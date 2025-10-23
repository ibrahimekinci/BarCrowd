# BarCrowd -  Project Overview
This project was completed for a school assessment. BarCrowd Melbourne is a crowd-sourced mobile application designed to provide real-time updates on crowd density, queue length, and age range for bars and clubs in Melbourne

**Target Audience:** Young adults (20–35) seeking timely, convenient information for planning nightlife activities.

## Technical Requirements & Constraints
*Development Language:** **Java** (Kotlin is strictly prohibited).
*IDE:** Android Studio.
*Architecture:** MVVM (Model-View-ViewModel) using Jetpack components.
*UI/Layout:** Fragments, Jetpack Navigation, and **Constraint Layout**.
*Database:** **Firebase** (Server-side) and/or Jetpack Room (Local caching) for persistence.

## Architecture and Layering (MVVM)
The application follows the recommended **MVVM** architecture to achieve separation of concerns.

| Layer | Responsibility | Key Components |
| :--- | :--- | :--- |
| **Presentation** | Handles UI rendering, user input, and navigation. | Activities, **Fragments**, Layout XML (Constraint Layout), **Jetpack Navigation**. |
| **ViewModel** | Stores UI-related data and handles configuration changes. Interacts with the Domain/Data layer. | [cite_start]**ViewModel**, **LiveData**, **Observers**. |
| **Data/Domain** | Responsible for application data (local/remote). | Repository (the single source of truth), Data Sources (**Firebase/Room**). |
