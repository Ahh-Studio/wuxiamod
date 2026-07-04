package com.aiden.wuxia.client.renderer.block_entity;

import com.aiden.wuxia.block.entity.JianghuPortalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class JianghuPortalRenderer implements BlockEntityRenderer<JianghuPortalBlockEntity, BlockEntityRenderState> {
    private static final Vector3fc FROM = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3fc TO = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final int PORTAL_COLOR = 0x55E0E0E0;
    private static final Map<Direction, List<Vector3fc>> FACES = Util.makeEnumMap(
            Direction.class,
            direction -> {
                FaceInfo faceInfo = FaceInfo.fromFacing(direction);
                return List.of(
                        faceInfo.getVertexInfo(0).select(FROM, TO),
                        faceInfo.getVertexInfo(1).select(FROM, TO),
                        faceInfo.getVertexInfo(2).select(FROM, TO),
                        faceInfo.getVertexInfo(3).select(FROM, TO)
                );
            }
    );

    private static final Transformation TRANSFORMATION = new Transformation(
            new Vector3f(0.0F, 0.375F, 0.0F),
            null,
            new Vector3f(1.0F, 0.375F, 1.0F),
            null
    );

    public JianghuPortalRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(final BlockEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(TRANSFORMATION);
        submitTopFace(poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    private static void submitTopFace(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector) {
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) -> {
            List<Vector3fc> faceVertices = FACES.get(Direction.UP);

            for (Vector3fc faceVertex : faceVertices) {
                buffer.addVertex(pose, faceVertex).setColor(PORTAL_COLOR);
            }

            for (int i = faceVertices.size() - 1; i >= 0; i--) {
                buffer.addVertex(pose, faceVertices.get(i)).setColor(PORTAL_COLOR);
            }
        });
    }
}
