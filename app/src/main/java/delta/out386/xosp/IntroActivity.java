package delta.out386.xosp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/*
 * This file is part of XOSPDelta.
 *
 * XOSPDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * XOSPDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with XOSPDelta. If not, see <http://www.gnu.org/licenses/>.
 */
public class IntroActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance(
                "Say 'Goodbye' to downloading full ROMs",
                "With deltas, you will never need to download huge ROM zips again.",
                R.drawable.slide1,
                Color.parseColor("#03A9F4")
        ));
        addSlide(AppIntroFragment.newInstance(
                "ROM updated? No worries!",
                "Just grab a tiny delta of the update, combine with your old ROM zip, and flash to get the update!",
                R.drawable.slide2,
                Color.parseColor("#FFC107")
        ));
        addSlide(AppIntroFragment.newInstance(
                "Quick info",
                "Once an update releases, copy the old ROM zip you already have, and the new delta, to the 'XOSPDelta' directory in root of storage. This is only needed the first time. For the next updates, copy over just the new delta.",
                R.drawable.slide3,
                Color.parseColor("#F44336")
        ));
        addSlide(AppIntroFragment.newInstance(
                "Quick info",
                "Once the delta is applied, just flash the zip it just created.",
                R.drawable.slide3,
                Color.parseColor("#FF9800")
        ));
        addSlide(AppIntroFragment.newInstance(
                "That's it!",
                "Everything will be automated from the next version, but for now, take a peek in Settings before starting.",
                R.drawable.slide4,
                Color.parseColor("#795548")
        ));
        setFadeAnimation();
    }
    @Override
    public void onNextPressed() {}
    @Override
    public void onDonePressed() {
        finish();
    }
    @Override
    public void onSlideChanged() {}
}
