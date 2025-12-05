package com.example.locators;

import org.openqa.selenium.By;

/**
 * Dedicated locator container for the Level-2 information section that follows the
 * main onboarding flow.
 */
public final class L2_Info_Locators {

    private L2_Info_Locators() {


    }

    public static final By SUCCESS_MESSAGE = By.xpath("//android.widget.TextView[@text='Successfully Captured']");
    public static final By POST_SUCCESS_COMPOSE_VIEW = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[6]");
    public static final By L2_CUSTOMER_GRID = By.id("mifix.io.qa:id/listview");
    public static final By LISTVIEW_OF_L2 = By.xpath("//android.widget.GridView[@resource-id='mifix.io.qa:id/listview']//android.view.ViewGroup//androidx.cardview.widget.CardView");
    public static final By ALERT_MESSAGE = By.id("mifix.io.qa:id/tv_alert_message");
    public static final By ALERT_OK_TEXT = By.id("mifix.io.qa:id/bt_ok");
    public static final By ALERT_DISMISS_BUTTON = By.xpath("//android.widget.Button[@text='OK' or @text='Ok' or @text='Close' or @text='CLOSE']");
    public static final By PROSPECT_L1_DETAILS_CARD = By.xpath("//androidx.cardview.widget.CardView[@resource-id='mifix.io.qa:id/cardL1Details']/android.view.ViewGroup");
    public static final By ALTERNATE_MOBILE_INPUT = By.xpath("//android.widget.EditText");
    public static final By CURRENT_ADDRESS_YES_TOGGLE = By.xpath("//android.widget.TextView[@text='Yes']");
    public static final By RELIGION_SPINNER = By.xpath("//android.widget.Spinner[@text='Select Religion']");
    public static final By RELIGION_OPTION_HINDU = By.xpath("//android.view.ViewGroup[@resource-id='android:id/content']/android.view.View/android.view.View/android.view.View/android.widget.ScrollView/android.widget.ScrollView/android.view.View[2]");
    public static final By EDUCATION_SPINNER = By.xpath("//android.widget.Spinner[@text='Select Education Qualification']");
    public static final By EDUCATION_OPTION_ENGINEERING_GRADUATE = By.xpath("//android.widget.TextView[@text='Engineering Graduate']");
    public static final By RESIDENCE_SPINNER = By.xpath("//android.widget.Spinner[@text='Select Nature of Residence']");
    public static final By RESIDENCE_OPTION_OWN_HOUSE = By.xpath("//android.view.ViewGroup[@resource-id='android:id/content']/android.view.View/android.view.View/android.view.View/android.widget.ScrollView/android.widget.ScrollView/android.view.View[1]");
    public static final By OWNERSHIP_PROOF_SPINNER = By.xpath("//android.widget.Spinner[@text='Select Ownership Proof']");
    public static final By OWNERSHIP_OPTION_KATHA = By.xpath("//android.view.ViewGroup[@resource-id='android:id/content']/android.view.View/android.view.View/android.view.View/android.widget.ScrollView/android.widget.ScrollView/android.view.View[2]");
    public static final By DOCUMENT_CAPTURE_SECTION = By.xpath("//android.widget.ScrollView/android.view.View");
    public static final By CAPTURE_FRONT_IMAGE_TEXT = By.xpath("//android.widget.TextView[@text='Capture Front image']");
    public static final By CAPTURE_BACK_IMAGE_TEXT = By.xpath("//android.widget.TextView[@text='Capture Back image']");
    public static final By SUBMIT_TEXT_BUTTON = By.xpath(
        "//android.widget.TextView[@text='SUBMIT' or @text='Submit']/ancestor::android.view.View[@clickable='true'][1]"
            + " | //android.widget.Button[@text='SUBMIT' or @text='Submit']"
            + " | //androidx.compose.ui.platform.ComposeView//android.view.View[@clickable='true' and descendant::android.widget.TextView[@text='SUBMIT' or @text='Submit']]"
    );
    public static final By GENERIC_COMPOSE_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View");
    // public static final By FINAL_SUBMIT_TEXT = By.xpath("//android.widget.TextView[@text='SUBMIT' or @text='Submit'] | //androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View");
    // public static final By FINAL_OKAY_TEXT = By.xpath("//android.widget.TextView[contains(@text,'OK')]");
 public static final By FINAL_SUBMIT_TEXT = By.xpath(
        "//android.widget.TextView[@text='SUBMIT' or @text='Submit']/ancestor::android.view.View[@clickable='true'][1]"
            + " | //androidx.compose.ui.platform.ComposeView//android.view.View[@clickable='true' and descendant::android.widget.TextView[@text='SUBMIT' or @text='Submit']]"
    );
    public static final By FINAL_OKAY_TEXT = By.xpath(
        "//android.widget.TextView[contains(@text,'OK')]/ancestor::android.view.View[@clickable='true'][1]"
            + " | //androidx.compose.ui.platform.ComposeView//android.view.View[@clickable='true' and descendant::android.widget.TextView[contains(@text,'OK')]]"
    );

    public static final By ADD_BANK_ACCOUNT_BUTTON = By.id("mifix.io.qa:id/im_add_bank_account_details");
    public static final By BANK_ACCOUNT_NUMBER_INPUT = By.id("mifix.io.qa:id/ed_acc_num");
    public static final By BANK_ACCOUNT_REENTER_INPUT = By.id("mifix.io.qa:id/ed_re_enter_acc_num");
    public static final By BANK_IFSC_INPUT = By.id("mifix.io.qa:id/ed_ifsc_code");
    public static final By BANK_IFSC_SEARCH_BUTTON = By.id("mifix.io.qa:id/bt_ifsc_search");
    public static final By BANK_BRANCH_NAME_INPUT = By.id("mifix.io.qa:id/ed_branch_name");
    public static final By BANK_BRANCH_ADDRESS_INPUT = By.id("mifix.io.qa:id/ed_branch_address");
    public static final By BANK_PROOF_IMAGE_TOGGLE = By.id("mifix.io.qa:id/add_or_view_bank_proof_image");
    public static final By BANK_PROOF_COMPOSE_CAPTURE = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View[1]/android.view.View");
    public static final By BANK_PROOF_SECOND_CAPTURE_COMPOSE = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View[1]/android.view.View[1]");
    public static final By CAMERA_CAPTURE_BUTTON = By.xpath("//android.widget.ImageButton[@content-desc='Capture']");
    public static final By MLKIT_CONFIRM_CROP_BUTTON = By.xpath("//android.widget.Button[@resource-id='com.google.android.gms.optional_mlkit_docscan_ui:id/confirm_crop_button']");
    public static final By MLKIT_NEXT_BUTTON = By.xpath("//android.widget.Button[@content-desc='Next']");
    public static final By BANK_PROOF_UPLOAD_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View[2]/android.widget.Button");
    public static final By BANK_PROOF_PREVIEW_DISMISS_BUTTON = By.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.widget.Button");
    public static final By BANK_PROOF_PREVIEW_CLOSE_ICON = By.id("mifix.io.qa:id/iv_close");
    public static final By BANK_PROOF_SAVE_BUTTON = By.id("mifix.io.qa:id/bt_save");
    public static final By BANK_ACCOUNT_DETAILS_CARD = By.xpath("//androidx.cardview.widget.CardView[@resource-id='mifix.io.qa:id/cvBankAccountDetails']/android.view.ViewGroup");


    



}
