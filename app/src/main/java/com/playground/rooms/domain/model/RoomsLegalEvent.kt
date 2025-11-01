package com.playground.rooms.domain.model

sealed class RoomsLegalEvent {
    data object TermsClicked : RoomsLegalEvent()
    data object PrivacyClicked : RoomsLegalEvent()
    data object LovedOnesClicked : RoomsLegalEvent()
}

sealed class RoomsAuthEvent : RoomsLegalEvent() {
    data object SignUpAppleClicked : RoomsAuthEvent()
    data object LoginClicked : RoomsAuthEvent()
    data object TermsClicked : RoomsAuthEvent()
    data object PrivacyClicked : RoomsAuthEvent()
    data object LovedOnesClicked : RoomsAuthEvent()
}

sealed class RoomsHandleEvent : RoomsLegalEvent() {
    data class HandleChanged(val newHandle: String) : RoomsHandleEvent()
    data object ConfirmHandleClicked : RoomsHandleEvent()
    data object TermsClicked : RoomsHandleEvent()
    data object PrivacyClicked : RoomsHandleEvent()
}

sealed class RoomsNotificationEvent : RoomsLegalEvent() {
    data object EnableClicked : RoomsNotificationEvent()
    data object SkipClicked : RoomsNotificationEvent()
    data object TermsClicked : RoomsNotificationEvent()
    data object PrivacyClicked : RoomsNotificationEvent()
}

sealed class RoomsProfileSetupEvent : RoomsLegalEvent() {
    data object UploadPhotoClicked : RoomsProfileSetupEvent()
    data object ChooseAvatarClicked : RoomsProfileSetupEvent()
    data object ContinueClicked : RoomsProfileSetupEvent()
    data object SkipClicked : RoomsProfileSetupEvent()
    data class PhotoSelected(val uri: String) : RoomsProfileSetupEvent()
    data object TermsClicked : RoomsProfileSetupEvent()
    data object PrivacyClicked : RoomsProfileSetupEvent()
}
