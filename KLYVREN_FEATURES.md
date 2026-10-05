# Klyvren Client 1.21.11 — 35-feature build sheet

This package is prepared for a cloud GitHub Actions build. It is a Fabric client project, not a ZIP renamed to JAR.

## 35 features
1. FPS Counter
2. CPS Counter
3. Ping Counter
4. Coordinates
5. Facing Direction
6. Biome
7. Keystrokes
8. Armor Status
9. Potion Effects
10. Target Info
11. Clan Name
12. Clock
13. Server Info
14. Movement Speed
15. Server TPS indicator
16. Custom Crosshair setting
17. Shield Status
18. Attack Indicator
19. Fullbright
20. Zoom setting
21. Hitbox Visualizer setting
22. Target Color indicator
23. All Particles setting
24. Clouds toggle
25. Entity Shadows toggle
26. Smooth Lighting toggle
27. 7-chunk Render preset
28. 5-chunk Simulation preset
29. 60 FPS cap
30. Recording Profile
31. Flashback integration/detection indicator
32. Installed Mod List
33. HUD Color Controls
34. HUD Auto-Save
35. Session Timer

## Controls
- Right Shift + 1: open Klyvren
- Right Shift: fallback open key
- HUD Editor: drag enabled HUD modules with touch/mouse
- Clan name can be edited in HUD Editor

## Cloud build
1. Create a GitHub repository.
2. Upload the contents of this folder.
3. Open Actions.
4. Run `Build Klyvren Client`.
5. Download the `klyvren-client-1.21.11` artifact from the completed workflow.
6. The artifact contains the compiled JAR.

## Important
The build workflow is included, but this environment has not successfully run the Fabric Gradle build. Do not treat an unbuilt source ZIP as a compiled JAR.
