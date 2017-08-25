package com.shing.aputimetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by Shing on 23-Aug-17.
 */

public class AboutActivity extends MaterialAboutActivity {

    private SharedPreferences prefs;
    private int colorIcon;
    private boolean useDarkTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useDarkTheme = prefs.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
            colorIcon = R.color.mal_color_icon_dark_theme;
        } else {
            colorIcon = R.color.mal_color_icon_light_theme;
        }
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        MaterialAboutList.Builder aboutListBuilder = new MaterialAboutList.Builder();


        //App Card
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc(R.string.copyright_text)
                .icon(R.mipmap.ic_launcher)
                .build());
        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(this,
                new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_information_outline)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18),
                getString(R.string.version_text), true));
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.changelog_text)
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_history)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18))
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        showChangelogDialog();
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.mal_title_licenses)
                .subText(R.string.license_apache2_text)
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_book)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18))
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        showLicenseDialog();
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.notices_title)
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_android)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18))
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        showLibrariesDialog();
                    }
                })
                .build());

        //Author card
        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.author_text);
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.author_name)
                .subText(R.string.author_info)
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_account)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.fork_text)
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(ContextCompat.getColor(this, colorIcon))
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(this, Uri.parse(getString(R.string.github_link))))
                .build());

        aboutListBuilder.addCard(appCardBuilder.build());
        aboutListBuilder.addCard(authorCardBuilder.build());
        return aboutListBuilder.build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.about_title);
    }

    private void showChangelogDialog() {

        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0) {
            accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        }
        ChangelogDialog.create(useDarkTheme, accentColor).show(getSupportFragmentManager(), "changelog");

    }

    private void showLicenseDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.license_text)
                .content(R.string.license_apache2_summary, "2017", getString(R.string.author_name))
                .positiveText(android.R.string.ok)
                .show();
    }

    private void showLibrariesDialog() {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.licenses)
                .setTitle(R.string.notices_title)
                .setNoticesCssStyle(getString(R.string.license_dialog_style)
                        .replace("{bg-color}", useDarkTheme ? "424242" : "ffffff")
                        .replace("{text-color}", useDarkTheme ? "ffffff" : "000000")
                        .replace("{license-bg-color}", useDarkTheme ? "535353" : "eeeeee")
                )
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }
}
