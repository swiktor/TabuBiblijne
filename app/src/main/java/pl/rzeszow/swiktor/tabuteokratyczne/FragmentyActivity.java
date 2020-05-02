package pl.rzeszow.swiktor.tabuteokratyczne;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

import pl.rzeszow.swiktor.tabuteokratyczne.fragmenty.GraFragment;
import pl.rzeszow.swiktor.tabuteokratyczne.fragmenty.ProfilFragment;
import pl.rzeszow.swiktor.tabuteokratyczne.fragmenty.RankingFragment;

public class FragmentyActivity extends AppCompatActivity implements NarzedziaWspolne.TitleChangeListener {

    Button fragGraButton;
    Button fragRankingButton;
    Button wylogujButton;
    Button profilButton;

    private RelativeLayout mLeftMenu = null;
    private RelativeLayout mNavigationBar = null;
    private RelativeLayout mMovableArea = null;
    private ImageButton mBtnMenu = null;


    private int mLeftMenuWidth = 0;
    private boolean mMenuSliding = false;
    private boolean mMenuOpen = false;
    private int mMenuPosition = 0;
    private View mRootLayout = null;


    private float mMenuSlideStartX = 0;
    private long mMenuSlideStartTime = 0;

    private static final long MENU_FLING_TRIGGER_TIME = 200;
    private static final int MENU_SLIDE_TRIGGER_DP = 5;
    private static final int MENU_EDGE_TRIGGER_DP = 30;
    private static final int MENU_FLING_TRIGGER_DP = 25;

    protected boolean handleSlideEnabled = true;

    private TextView pageTitle;
    String personId = "";
    String imie_nazwisko = "";
    String email = "";
    String punkty = "";
    String zdjecieURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmenty);

        fragGraButton = (Button) findViewById(R.id.frag_gra);
        fragRankingButton = (Button) findViewById(R.id.frag_ranking);
        wylogujButton = (Button) findViewById(R.id.wyloguj); 
        profilButton = (Button) findViewById(R.id.profil);

        personId = getIntent().getStringExtra("personId");
        zdjecieURL = getIntent().getStringExtra("zdjecieURL");
        email = getIntent().getStringExtra("email");
        imie_nazwisko = getResources().getString(R.string.witaj) + " " + getIntent().getStringExtra("imie") + " " + getIntent().getStringExtra("nazwisko");
        punkty = getIntent().getStringExtra("zwrotka");
        punkty = getResources().getString(R.string.masz) + " " + punkty + " " + getResources().getString(R.string.pkt);

        fragGraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                domyslnyFragment();
            }
        });

        fragRankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = RankingFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
                closeMenu();
            }
        });

        profilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ProfilFragment.newInstance();
                Bundle args = new Bundle();
                args.putString("zdjecieURL", zdjecieURL);
                args.putString("imie_nazwisko", imie_nazwisko);
                args.putString("email", email);
                args.putString("punkty", punkty);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
                closeMenu();
            }
        });

        wylogujButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wyloguj();
            }
        });


        pageTitle = (TextView) findViewById(R.id.pageTitle);
        mRootLayout = (View) findViewById(R.id.root);

        mLeftMenu = (RelativeLayout) findViewById(R.id.leftMenu);
        if (mLeftMenu != null) {

            mNavigationBar = (RelativeLayout) findViewById(R.id.navigationBar);
            mMovableArea = (RelativeLayout) findViewById(R.id.movableArea);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;

            mLeftMenuWidth = 4 * widthPixels / 5;
            updateLeftMenu(0);


            mBtnMenu = (ImageButton) findViewById(R.id.btnMenu);
            if (mBtnMenu != null) {
                mBtnMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMenuOpen) {
                            if (mMenuPosition == mLeftMenuWidth) {
                                closeMenu();
                            }
                        } else {
                            if (mMenuPosition == 0) {
                                updateLeftMenu(1);
                                openMenu();
                            }
                        }
                    }
                });

                mBtnMenu.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        handleSlide(motionEvent, false, false);
                        return false;
                    }
                });
            }


            if (mRootLayout != null) {
                mRootLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        handleSlide(motionEvent, !mMenuOpen, mMenuOpen);
                        return true;
                    }
                });
            }
            mLeftMenu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    handleSlide(motionEvent, false, false);
                    return true;
                }
            });


        }

        domyslnyFragment();
    }

    public void openMenu() {
        startSlideAnimation(mLeftMenuWidth);
        mMenuOpen = true;
    }

    public void closeMenu() {
        startSlideAnimation(0);
        mMenuOpen = false;
    }

    private void updateLeftMenu(int position) {
        if (position < 0) {
            position = 0;
        } else if (position > mLeftMenuWidth) {
            position = mLeftMenuWidth;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLeftMenu.getLayoutParams();
        params.setMargins(-mLeftMenuWidth + position, 0, 0, 0);
        params.width = mLeftMenuWidth;
        mLeftMenu.setLayoutParams(params);
        mLeftMenu.requestLayout();

        if (mNavigationBar != null) {
            params = (RelativeLayout.LayoutParams) mNavigationBar.getLayoutParams();
            params.setMargins(0, 0, -position, 0);
            mNavigationBar.setLayoutParams(params);
            mNavigationBar.requestLayout();
        }

        if (mMovableArea != null) {
            params = (RelativeLayout.LayoutParams) mMovableArea.getLayoutParams();
            params.setMargins(position, 0, -position, 0);
            mMovableArea.setLayoutParams(params);
            mMovableArea.requestLayout();
        }

        mMenuPosition = position;
    }

    private void startSlideAnimation(int finalPosition) {
        final SlideAnimation animation = new SlideAnimation(mMenuPosition, finalPosition);
        mLeftMenu.post(new Runnable() {
            @Override
            public void run() {
                mLeftMenu.startAnimation(animation);
            }
        });
    }

    public boolean isMenuOpen() {
        return mMenuOpen;
    }

    public void handleSlide(MotionEvent motionEvent, boolean fromLeftEdge, boolean fromRightEdge) {
        if (handleSlideEnabled) {
            float x = motionEvent.getRawX();
            float deltaDp = NarzedziaWspolne.pxToDp(x - mMenuSlideStartX, this);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mMenuSlideStartX = x;
                    mMenuSlideStartTime = new Date().getTime();
                    mMenuSliding = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMenuSliding) {
                        int pos = (mMenuOpen) ? (int) (x - mMenuSlideStartX + mLeftMenuWidth) : (int) (x - mMenuSlideStartX);
                        updateLeftMenu(pos);
                    } else {
                        if ((!mMenuOpen && (deltaDp >= MENU_SLIDE_TRIGGER_DP)) ||
                                (mMenuOpen && (deltaDp <= -MENU_SLIDE_TRIGGER_DP))) {
                            if (fromLeftEdge) {
                                if (NarzedziaWspolne.pxToDp(x, this) <= MENU_EDGE_TRIGGER_DP) {
                                    mMenuSliding = true;
                                }
                            } else if (fromRightEdge) {
                                if (x <= mLeftMenuWidth) {
                                    mMenuSliding = true;
                                } else {
                                    mMenuSlideStartX = x;
                                    mMenuSlideStartTime = new Date().getTime();
                                }
                            } else {
                                mMenuSliding = true;
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    long endTime = new Date().getTime();
                    if (endTime - mMenuSlideStartTime <= MENU_FLING_TRIGGER_TIME) {
                        if (!mMenuOpen &&
                                (deltaDp >= MENU_FLING_TRIGGER_DP) &&
                                (NarzedziaWspolne.pxToDp(mMenuSlideStartX, this) < MENU_EDGE_TRIGGER_DP)) {
                            openMenu();
                            break;
                        } else if (mMenuOpen && (deltaDp <= -MENU_FLING_TRIGGER_DP)) {
                            closeMenu();
                            break;
                        }
                    }
                    if (fromRightEdge && (x > mLeftMenuWidth)) {
                        closeMenu();
                        break;
                    }
                    if (mMenuPosition < (mLeftMenuWidth / 2)) {
                        closeMenu();
                    } else {
                        openMenu();
                    }
                    break;
            }
        }
    }

    @Override
    public void onTitleSet(String msg) {
        pageTitle.setText(msg);
    }


    private class SlideAnimation extends Animation {
        private static final float SPEED = 3.0f;

        private float mStart;
        private float mEnd;

        public SlideAnimation(float fromX, float toX) {
            mStart = fromX;
            mEnd = toX;

            setInterpolator(new DecelerateInterpolator());

            float duration = Math.abs(mEnd - mStart) / SPEED;
            setDuration((long) duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float position = (mEnd - mStart) * interpolatedTime + mStart;
            updateLeftMenu((int) position);
        }


    }

    private void domyslnyFragment() {
        Fragment fragment = GraFragment.newInstance();
        Bundle args = new Bundle();
        args.putString("personId", personId);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        closeMenu();
    }

    private void wyloguj() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent();
                        intent.setClass(FragmentyActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

}