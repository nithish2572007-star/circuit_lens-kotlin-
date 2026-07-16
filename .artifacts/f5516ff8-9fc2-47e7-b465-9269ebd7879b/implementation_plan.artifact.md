# Implementation Plan - Refactor CircuitLensApp UI

The goal is to refactor `CircuitLensApp.kt` by splitting it into smaller, manageable files categorized by their role (screens, components, navigation, etc.). This will improve code maintainability and readability.

## User Review Required

> [!IMPORTANT]
> This refactor will move many composables to new packages. I will ensure all imports are updated correctly. The main entry point `CircuitLensApp()` will remain in `CircuitLensApp.kt` but will now delegate to the newly created screen files.

## Proposed Changes

### Theme

#### [MODIFY] [Color.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/theme/Color.kt)
- Add colors defined in `CircuitLensApp.kt`: `DarkBg`, `CardBg`, `LimePrimary`, `LimeGradientEnd`, `BorderGreen`, `TextGray`.

### Navigation

#### [NEW] [Screen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/navigation/Screen.kt)
- Move the `Screen` enum here.

### Components

Move reusable UI components to `com.example.circuitlens.ui.components`.

#### [NEW] [CircuitHeader.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/CircuitHeader.kt)
#### [NEW] [CircuitInputField.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/CircuitInputField.kt)
#### [NEW] [CircuitButton.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/CircuitButton.kt)
#### [NEW] [AuthToggle.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/AuthToggle.kt)
#### [NEW] [CircuitLensBottomBar.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/CircuitLensBottomBar.kt) (includes `BottomNavItem`)
#### [NEW] [OverviewCard.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/OverviewCard.kt)
#### [NEW] [ActivityItem.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/ActivityItem.kt)
#### [NEW] [CollapsibleSection.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/components/CollapsibleSection.kt)

### Screens

Move individual screens to `com.example.circuitlens.ui.screens`.

#### [NEW] [LoginScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/LoginScreen.kt)
#### [NEW] [SignUpScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/SignUpScreen.kt)
#### [NEW] [HomeScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/HomeScreen.kt)
#### [NEW] [ScanScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/ScanScreen.kt)
#### [NEW] [ChatScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/ChatScreen.kt)
#### [NEW] [HistoryScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/HistoryScreen.kt)
#### [NEW] [ProfileScreen.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/screens/ProfileScreen.kt)

### Main Entry Point

#### [MODIFY] [CircuitLensApp.kt](file:///home/nithish/AndroidStudioProjects/CircuitLens/app/src/main/java/com/example/circuitlens/ui/CircuitLensApp.kt)
- Remove all moved components and screens.
- Keep only `CircuitLensApp()` composable and necessary imports.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure the project compiles with the new structure and updated imports.

### Manual Verification
- Deploy the app and navigate through all screens (Login, Sign Up, Home, Scan, Chat, History, Profile) to ensure UI remains functional and looks identical.
