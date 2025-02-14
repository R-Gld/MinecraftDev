/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mixin.insight

import com.demonwav.mcdev.platform.mixin.handlers.InjectorAnnotationHandler
import com.demonwav.mcdev.platform.mixin.handlers.MixinAnnotationHandler
import com.demonwav.mcdev.platform.mixin.util.isMixinEntryPoint
import com.demonwav.mcdev.util.toTypedArray
import com.intellij.codeInspection.reference.RefElement
import com.intellij.codeInspection.visibility.EntryPointWithVisibilityLevel
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiUtil
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element

class MixinEntryPoint : EntryPointWithVisibilityLevel() {

    @JvmField
    var mixinEntryPoint = true

    override fun getId() = "mixin"
    override fun getDisplayName() = "Mixin injectors"
    override fun getTitle() = "Suggest private visibility level for Mixin injectors"

    // TODO: support more handlers than the builtin
    // need to find a way to access the project for that
    override fun getIgnoreAnnotations() =
        MixinAnnotationHandler.getBuiltinHandlers()
            .filter { (_, handler) -> handler.isEntryPoint }
            .map { (name, _) -> name }
            .toTypedArray()

    override fun isEntryPoint(element: PsiElement) = isMixinEntryPoint(element)

    override fun isEntryPoint(refElement: RefElement, psiElement: PsiElement) = isEntryPoint(psiElement)

    override fun getMinVisibilityLevel(member: PsiMember): Int {
        if (member !is PsiMethod) {
            return -1
        }
        val project = member.project
        for (annotation in member.annotations) {
            val qName = annotation.qualifiedName ?: continue
            val handler = MixinAnnotationHandler.forMixinAnnotation(qName, project)
            if (handler is InjectorAnnotationHandler) {
                return PsiUtil.ACCESS_LEVEL_PRIVATE
            }
        }
        return -1
    }

    override fun isSelected() = mixinEntryPoint
    override fun setSelected(selected: Boolean) {
        mixinEntryPoint = selected
    }

    override fun readExternal(element: Element) = XmlSerializer.serializeInto(this, element)
    override fun writeExternal(element: Element) = XmlSerializer.serializeInto(this, element)
}
