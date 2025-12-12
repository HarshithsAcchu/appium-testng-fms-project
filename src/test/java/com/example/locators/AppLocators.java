package com.example.locators;

import org.openqa.selenium.By;

// Removed MobileBy usages to avoid editor classpath issues; use standard `By` where possible

public final class AppLocators {

    private AppLocators() {
    }

    public static final class Home {
        private Home() {
        }

        public static final By JLG_TEXT = By.xpath("//*[contains(@text,'JLG')]");
    }

    public static final class Login {
        private Login() {
        }

        public static final By USER_ID = By.id("com.nst.profile.qa:id/editUserId");
        public static final By PASSWORD = By.id("com.nst.profile.qa:id/editPassword");
        public static final By SIGN_IN = By.id("com.nst.profile.qa:id/buttonSignIn");
        public static final By SIGN_IN_FALLBACK = By.className("android.widget.Button");
    }

    public static final class Onboarding {
        private Onboarding() {
        }

        // AccessibilityId maps to content-desc on Android; use xpath to match it
        public static final By VALIDATE_ACCESSIBILITY = By.xpath("//*[@content-desc='Validate']");
        public static final By VALIDATE_PARENT = By.xpath("//android.widget.TextView[@text='Validate Number']/ancestor::android.view.View[@clickable='true'][1]");
        public static final By VALIDATE_PARENT_FUZZY = By.xpath("//android.widget.TextView[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VALIDATE')]/ancestor::android.view.View[@clickable='true'][1]");
        public static final By VALIDATE_CLICKABLE = By.xpath("//*[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VALIDATE') and (@clickable='true' or @clickable='True')]");
        public static final By VALIDATE_RESOURCE = By.xpath("//*[contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VALIDATE') and (@clickable='true' or @clickable='True')]");
        public static final By SUBMIT_RESOURCE = By.xpath("//*[contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'SUBMIT') and (@clickable='true' or @clickable='True')]");
        public static final By BOUNDS_FALLBACK = By.xpath("//android.view.View[@bounds='[99,1534][981,1677]' and (@clickable='true' or @clickable='True')]");
        public static final By FIRST_CLICKABLE = By.xpath("(//*[(@clickable='true' or @clickable='True')])[1]");
        public static final By VALIDATE_TEXT_GENERIC = By.xpath(
            "//*[" +
                "contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VALIDATE')" +
                " or contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VALIDATE')" +
            "]"
        );

        public static final By CUSTOMER_DETAILS_TEXT = By.xpath("//android.widget.TextView[@text='Customer Details']");
        public static final By CAPTURE_CUSTOMER_CONSENT = By.xpath("//android.widget.TextView[@text='CAPTURE CUSTOMER CONSENT']");
        public static final By MOBILE_INPUT_BY_LABEL = By.xpath("//android.widget.EditText[../android.widget.TextView[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'MOBILE')]]");
        public static final By OTP_INPUT_BY_ID = By.xpath("//android.widget.EditText[contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP')]");
        public static final By OTP_INPUT_BY_HINT = By.xpath("//android.widget.EditText[contains(translate(@hint,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP')]");
        public static final By OTP_INPUT_BY_DESC = By.xpath("//android.widget.EditText[contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP')]");
        public static final By OTP_PIN_VIEW_DIGITS = By.xpath("//android.widget.EditText[@password='true' or contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'PIN')]");
        public static final By RIGHT_ARROW_LOCATOR = By.xpath("//android.view.View[@bounds='[430,1848][650,2068]' and @clickable='true']");
        public static final By RIGHT_ARROW_LOCATOR_FUZZY = By.xpath(
            "//*[contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'RIGHT') " +
                "and contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'ARROW') " +
                "and (@clickable='true' or @clickable='True')]"
        );
        public static final By RIGHT_ARROW_IMAGE_FUZZY = By.xpath(
            "//android.widget.ImageView[contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'RIGHT') " +
                "and contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'ARROW')]"
        );
        public static final By OTP_GENERIC_EDITTEXT = By.xpath("//android.widget.EditText[contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP') " +
            "or contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'PIN') " +
            "or contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'CODE') " +
            "or contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP')]");
        public static final By OTP_TEXT_HINTS = By.xpath("//*[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OTP') " +
            "or contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'PIN') " +
            "or contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'VERIFICATION') " +
            "or contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'CODE')]");
        public static final By PROGRESS_BAR_GENERIC = By.xpath("//*[contains(translate(@class,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'PROGRESS') " +
            "or contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'PROGRESS') " +
            "or contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'LOADER')]");
        public static final By DIALOG_GENERIC = By.xpath("//*[contains(translate(@resource-id,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'DIALOG') " +
            "or contains(translate(@class,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'DIALOG') " +
            "or contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'ALLOW')]");
        public static final By TOAST_GENERIC = By.xpath("//android.widget.Toast");

        // Capture / document upload / voter-id flow
        public static final By CAMERA_CAPTURE_BUTTON = By.id("mifix.io.qa:id/btnCapture");
        public static final By CROP_BUTTON = By.xpath("//android.widget.Button[@content-desc='Crop']");
        public static final By CAMERA_CANCEL_BUTTON = By.id("mifix.io.qa:id/bt_cancel");
        public static final By UPLOAD_RECENT_IMAGE_TEXT = By.xpath("//android.widget.TextView[@text='Upload Recent Image']");
        public static final By COMPOSE_THIRD_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View[3]");
        public static final By SELECT_DOCUMENT_SPINNER = By.xpath("//android.widget.Spinner[@text='Select Document']");
        public static final By DOCUMENT_FIRST_OPTION = By.xpath("//android.view.ViewGroup[@resource-id='android:id/content']/android.view.View/android.view.View/android.view.View/android.widget.ScrollView/android.widget.ScrollView/android.view.View[1]");
        public static final By CAPTURE_FRONT_IMAGE_TEXT = By.xpath("//android.widget.TextView[@text='Capture Front image']");
        public static final By COMPOSE_GENERIC_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View");
        public static final By VOTER_ID_INPUT = By.xpath("//android.widget.EditText[.//android.widget.TextView[@text='Enter Voter Id number']]");
        public static final By SUBMIT_SECTION_BUTTON = By.xpath("//android.view.View[.//android.widget.TextView[@text='SUBMIT']]//android.widget.Button");
        public static final By COMPOSE_SECOND_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View[2]");
        public static final By SCROLL_EDITTEXT_FIVE = By.xpath("//android.widget.ScrollView/android.widget.EditText[3]");
        public static final By SCROLL_EDITTEXT_SIX = By.xpath("//android.widget.ScrollView/android.widget.EditText[4]");
        public static final By SCROLL_RADIO_BUTTON_THREE = By.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.RadioButton");
        public static final By SCROLL_VIEW2_INNER = By.xpath("//android.widget.ScrollView/android.view.View[2]/android.view.View");
        public static final By SCROLL_VIEW3_INNER = By.xpath("//android.widget.ScrollView/android.view.View[3]/android.view.View");
        public static final By SCROLL_VIEW4_SECTION = By.xpath("//android.widget.ScrollView/android.view.View[4]");
        public static final By COMPOSE_NESTED_SECOND_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");
        public static final By SCROLL_VIEW7_BUTTON = By.xpath("//android.widget.ScrollView/android.view.View[7]/android.widget.Button");
        public static final By SUCCESSFULLY_CAPTURED_MESSAGE = By.xpath("//android.widget.TextView[@text='Successfully Captured']");
    }

    public static final class Permissions {
        private Permissions() {
        }

        public static final String ALLOW_BUTTON_TEXT_TEMPLATE = "//android.widget.Button[@text='%s']";
        public static final By ALLOW_BUTTON_RESOURCE_CONTAINS = By.xpath("//android.widget.Button[contains(@resource-id,'permission_allow')]");
        public static final String[] ALLOW_BUTTON_IDS = {
            "com.android.permissioncontroller:id/permission_allow_button",
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.android.permissioncontroller:id/permission_allow_one_time_button",
            "com.android.packageinstaller:id/permission_allow_button",
            "android:id/button1"
        };

        public static final String[] ALLOW_BUTTON_TEXTS = {
            "Allow",
            "ALLOW",
            "Allow only while using the app",
            "While using the app",
            "Allow while using app",
            "Grant",
            "OK"
        };
    }

    public static By byId(String id) {
        return By.id(id);
    }

    public static By permissionsButtonByText(String text) {
        return By.xpath(String.format(Permissions.ALLOW_BUTTON_TEXT_TEMPLATE, text));
    }
}
