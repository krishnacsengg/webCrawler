One of the users has raised a concern regarding a timestamp mismatch between the EDP screen and the File Manager. Currently, EDP shows timestamps in UTC, while the File Manager (built in Angular) displays them in the user's system timezone.

To address this inconsistency, can we create a story to update the time details in the Run & Monitor screens as per the user profile's timezone setting? Additionally, this timezone value can be passed as a parameter to the File Manager, which we will then use to update and display the timestamps accordingly.
