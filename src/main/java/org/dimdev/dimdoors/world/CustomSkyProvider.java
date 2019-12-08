//package org.dimdev.dimdoors.world;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.multiplayer.WorldClient;
//import net.minecraft.client.renderer.*;
//import net.minecraft.client.renderer.vertex.*;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraftforge.client.IRenderHandler;
//import org.lwjgl.opengl.GL11;
//
//public class CustomSkyProvider extends IRenderHandler {
//
//    private static final Identifier locationEndSkyPng = new Identifier("textures/environment/end_sky.png");
//    int starGLCallList;
//    int glSkyList;
//    int glSkyList2;
//
//    public Identifier getMoonRenderPath() {
//        return null;
//    }
//
//    public Identifier getSunRenderPath() {
//        return null;
//    }
//
//
//    @Environment(EnvType.CLIENT)
//    @Override
//    public void render(float partialTicks, WorldClient world, Minecraft mc) {
//
//        starGLCallList = GLAllocation.generateDisplayLists(3);
//        glSkyList = starGLCallList + 1;
//        glSkyList2 = starGLCallList + 2;
//        RenderSystem.disableFog();
//        RenderSystem.disableAlpha();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        RenderHelper.disableStandardItemLighting();
//        RenderSystem.depthMask(false);
//
//        MinecraftClient.getInstance().getTextureManager().bindTexture(locationEndSkyPng);
//
//        if (mc.world.dimension.isSurfaceWorld()) {
//            RenderSystem.disableTexture2D();
//            final Vec3d vec3 = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
//            float f1 = (float) vec3.x;
//            float f2 = (float) vec3.y;
//            float f3 = (float) vec3.z;
//            float f4;
//
//            if (mc.gameSettings.anaglyph) {
//                float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
//                float f6 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
//                f4 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
//                f1 = f5;
//                f2 = f6;
//                f3 = f4;
//            }
//
//            RenderSystem.color(f1, f2, f3);
//            final Tessellator tessellator = Tessellator.getInstance();
//            final BufferBuilder buffer = tessellator.getBuffer();
//            RenderSystem.depthMask(false);
//            RenderSystem.enableFog();
//            RenderSystem.color(f1, f2, f3);
//            RenderSystem.callList(glSkyList);
//            RenderSystem.disableFog();
//            RenderSystem.disableAlpha();
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            RenderHelper.disableStandardItemLighting();
//            float[] afloat = world.dimension.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
//            float f7;
//            float f8;
//            float f9;
//            float f10;
//
//            if (afloat != null) {
//                RenderSystem.disableTexture2D();
//                RenderSystem.shadeModel(GL11.GL_SMOOTH);
//                RenderSystem.pushMatrix();
//                RenderSystem.rotate(90.0F, 1.0F, 0.0F, 0.0F);
//                RenderSystem.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
//                RenderSystem.rotate(90.0F, 0.0F, 0.0F, 1.0F);
//                f4 = afloat[0];
//                f7 = afloat[1];
//                f8 = afloat[2];
//                float f11;
//
//                if (mc.gameSettings.anaglyph) {
//                    f9 = (f4 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
//                    f10 = (f4 * 30.0F + f7 * 70.0F) / 100.0F;
//                    f11 = (f4 * 30.0F + f8 * 70.0F) / 100.0F;
//                    f4 = f9;
//                    f7 = f10;
//                    f8 = f11;
//                }
//
//                buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
//                buffer.color(f4, f7, f8, afloat[3]).pos(0.0, 100D, 0).endVertex();
//                byte b0 = 16;
//
//                for (int j = 0; j <= b0; ++j) {
//                    f11 = j * (float) Math.PI * 2.0F / b0;
//                    float f12 = MathHelper.sin(f11);
//                    float f13 = MathHelper.cos(f11);
//                    buffer.color(afloat[0], afloat[1], afloat[2], 0.0F).pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * afloat[3]).endVertex();
//                }
//
//                tessellator.draw();
//                RenderSystem.popMatrix();
//                RenderSystem.shadeModel(GL11.GL_FLAT);
//            }
//
//            RenderSystem.enableTexture2D();
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//            RenderSystem.pushMatrix();
//            f4 = 1.0F - world.getRainStrength(partialTicks);
//            f7 = 0.0F;
//            f8 = 0.0F;
//            f9 = 0.0F;
//            RenderSystem.color(1.0F, 1.0F, 1.0F, f4);
//            RenderSystem.translate(f7, f8, f9);
//            RenderSystem.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
//            RenderSystem.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
//            f10 = 30.0F;
//            MinecraftClient.getInstance().getTextureManager().bindTexture(getSunRenderPath());
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//            buffer.pos(-f10, 100, -f10).tex(0, 0).endVertex();
//            buffer.pos(f10, 100, -f10).tex(1, 0).endVertex();
//            buffer.pos(f10, 100, f10).tex(1, 1).endVertex();
//            buffer.pos(-f10, 100, f10).tex(0, 1).endVertex();
//            tessellator.draw();
//
//            f10 = 20.0F;
//            MinecraftClient.getInstance().getTextureManager().bindTexture(getMoonRenderPath());
//            int k = world.getMoonPhase();
//            int i = k % 4;
//            int i1 = k / 4 % 2;
//            float f16 = i + 1;
//            float f17 = i1 + 1;
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//            buffer.pos(-f10, -100, f10).tex(f16, f17).endVertex();
//            buffer.pos(f10, -100, f10).tex((float) i, f17).endVertex();
//            buffer.pos(f10, -100, -f10).tex((float) i, (float) i1).endVertex();
//            buffer.pos(-f10, -100, -f10).tex(f16, (float) i1).endVertex();
//            tessellator.draw();
//            RenderSystem.disableTexture2D();
//            float f18 = world.getStarBrightness(partialTicks) * f4;
//
//            if (f18 > 0.0F) {
//                RenderSystem.color(f18, f18, f18, f18);
//                RenderSystem.callList(starGLCallList);
//            }
//
//            RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.disableBlend();
//            RenderSystem.enableAlpha();
//            RenderSystem.enableFog();
//            RenderSystem.popMatrix();
//            RenderSystem.disableTexture2D();
//            RenderSystem.color(0.0F, 0.0F, 0.0F);
//            double d0 = mc.player.getLook(partialTicks).y - world.getHorizon();
//
//            if (d0 < 0.0D) {
//                RenderSystem.pushMatrix();
//                RenderSystem.translate(0.0F, 12.0F, 0.0F);
//                RenderSystem.callList(glSkyList2);
//                RenderSystem.popMatrix();
//                f8 = 1.0F;
//                f9 = -((float) (d0 + 65.0D));
//                f10 = -f8;
//
//                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
//                buffer.pos(-f8, f9, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f9, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f9, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f9, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f9, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f9, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f9, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f9, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(-f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, f8).color(0, 0, 0, 1).endVertex();
//                buffer.pos(f8, f10, -f8).color(0, 0, 0, 1).endVertex();
//                tessellator.draw();
//            }
//
//            if (world.dimension.isSkyColored()) {
//                RenderSystem.color(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
//            } else {
//                RenderSystem.color(f1, f2, f3);
//            }
//
//            RenderSystem.pushMatrix();
//            RenderSystem.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
//            RenderSystem.callList(glSkyList2);
//            RenderSystem.popMatrix();
//            RenderSystem.enableTexture2D();
//            RenderSystem.depthMask(true);
//        }
//    }
//}
