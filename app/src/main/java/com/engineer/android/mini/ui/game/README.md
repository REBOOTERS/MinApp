# Local Pool Game MVP

This package contains a single-player, offline pool prototype focused on touch feel and mobile UI.

## Entry

- Open app `RootActivity`
- Tap `pool_local`
- Game page is locked to landscape.
- Tap `Settings` to open the separate tuning page.

## Controls

- Drag on table to aim.
- Tap `Shoot` to fire the cue ball.
- Use `Power` slider to control shot force.
- On foul, drag cue ball to a legal position behind the head string (`ball in hand`).
- The game view is now separated from the tuning page.
- Tap `Reset` to rerack.

## Local Rules (MVP)

- 2 local players take turns (`You` and `Bot` placeholder).
- Open table at break; first legal potted group assigns `Solid` or `Stripe`.
- Cue ball pocket or illegal first hit counts as foul and switches turn.
- After legal first contact, if no ball touches a rail and nothing is pocketed, it is also foul.
- Fouled player gives opponent ball-in-hand placement.
- Potting your own group keeps turn; otherwise turn switches.
- Black 8 only wins when your group is cleared; otherwise you lose.

## Files

- `PoolGameActivity.kt`: host Activity and UI controls.
- `PoolSettingsActivity.kt`: separate tuning page.
- `LocalPoolGameView.kt`: render, physics update, collision, pockets, aiming, ball-in-hand.
- `activity_pool_game.xml`: landscape game HUD and control panel layout.
- `activity_pool_settings.xml`: standalone settings layout.

## Notes

- Current rules are simplified for fast prototyping (local two-player flow, simplified foul details).
- Physics is custom lightweight 2D simulation for quick iteration on mobile.
- Tuning slider values and ball-in-hand placement mode are persisted via DataStore and restored on next launch.