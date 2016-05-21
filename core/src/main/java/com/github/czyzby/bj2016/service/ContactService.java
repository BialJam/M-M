package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.bj2016.entity.Entity;

/** Listens to Box2D entity collisions.
 *
 * @author MJ */
@Component
public class ContactService implements ContactListener {
    @Override
    public void beginContact(final Contact contact) {
        final Object userDataA = contact.getFixtureA().getUserData();
        final Object userDataB = contact.getFixtureB().getUserData();
        if (userDataA != null && userDataB != null) {
            preCollide((Entity) userDataA, (Entity) userDataB);
        }
    }

    private static void preCollide(final Entity userDataA, final Entity userDataB) {
        userDataA.beginCollision(userDataB);
        userDataB.beginCollision(userDataA);
    }

    @Override
    public void endContact(final Contact contact) {
        final Object userDataA = contact.getFixtureA().getUserData();
        final Object userDataB = contact.getFixtureB().getUserData();
        if (userDataA != null && userDataB != null) {
            postCollide((Entity) userDataA, (Entity) userDataB);
        }
    }

    private static void postCollide(final Entity userDataA, final Entity userDataB) {
        userDataA.endCollision(userDataB);
        userDataB.endCollision(userDataA);
    }

    @Override
    public void preSolve(final Contact contact, final Manifold oldManifold) {
    }

    @Override
    public void postSolve(final Contact contact, final ContactImpulse impulse) {
    }
}
