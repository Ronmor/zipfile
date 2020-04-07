package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    private ArFragment fragment;
    private Uri selectedObject;
    private LinearLayout gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.gallery_layout);
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        InitializeGallery();

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane , MotionEvent motionEvent) -> {
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return; // for now lets practice vertical planes only
                    }
                   Anchor anchor = hitResult.createAnchor(); //translate from 2d to 3d

                    placeObject(fragment,anchor,selectedObject);
                }
        );
    }

    private void InitializeGallery(){

        ImageView cheese = new ImageView(this);
        cheese.setImageResource(R.drawable.cheese);
        cheese.setContentDescription("Cheese");
        cheese.setOnClickListener(view -> {selectedObject = Uri.parse( "cheese.sfb");});
        gallery.addView(cheese);


        ImageView milk = new ImageView(this);
        milk.setImageResource(R.drawable.milk);
        milk.setContentDescription("Milk");
        milk.setOnClickListener(view -> {selectedObject = Uri.parse("Jug of milk.sfb");});
        gallery.addView(milk);

        ImageView egg = new ImageView(this);
        egg.setImageResource(R.drawable.egg);
        egg.setContentDescription("Eggs");
        egg.setOnClickListener(view -> {selectedObject = Uri.parse("egg.sfb");});
        gallery.addView(egg);
    }

    /**
     *
     * @param fragment
     * @param anchor
     * @param model
     */
    private  void placeObject(ArFragment fragment, Anchor anchor, Uri model){
        ModelRenderable.builder()
                .setSource(fragment.getContext(),model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment,anchor,renderable))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Some error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

    }

    /**
     *
     * @param fragment
     * @param anchor
     * @param renderable
     */
    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable){
        AnchorNode anchorNode = new AnchorNode(anchor); // a node that cannot be interacted with
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem()); //a node that can be interacted with
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();

    }
}
