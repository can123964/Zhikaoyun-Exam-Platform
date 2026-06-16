import type { Directive, DirectiveBinding } from "vue";
import { hasRole } from "@/utils/auth";

/**
 * 按钮权限指令 — 基于角色值 0/1/2
 *
 * 使用注释节点占位替代 removeChild，确保元素在 updated 钩子中可恢复。
 * 当用户角色变更时（如切换账号），元素能正确显示/隐藏。
 *
 * @example v-hasPerm="2"        → 仅管理员可见
 * @example v-hasPerm="[1, 2]"   → 教师或管理员可见
 */

// WeakMap 存储被隐藏元素的占位注释节点
const placeholderMap = new WeakMap<Comment, HTMLElement>();
const elementMap = new WeakMap<HTMLElement, Comment>();

function checkPermission(el: HTMLElement, binding: DirectiveBinding) {
  const required = binding.value;
  if (required === undefined || required === null) return;

  const roles = Array.isArray(required) ? required : [required];
  const allowed = roles.some((r: number) => hasRole(r));

  const existingPlaceholder = elementMap.get(el);

  if (!allowed && !existingPlaceholder) {
    // 无权限且尚未隐藏 → 用注释节点占位并移除元素
    if (el.parentNode) {
      const comment = document.createComment(`v-hasPerm: ${JSON.stringify(roles)}`);
      el.parentNode.replaceChild(comment, el);
      placeholderMap.set(comment, el);
      elementMap.set(el, comment);
    }
  } else if (allowed && existingPlaceholder) {
    // 有权限且当前被隐藏 → 恢复元素
    if (existingPlaceholder.parentNode) {
      existingPlaceholder.parentNode.replaceChild(el, existingPlaceholder);
      placeholderMap.delete(existingPlaceholder);
      elementMap.delete(el);
    }
  }
}

export const hasPerm: Directive = {
  mounted: checkPermission,
  updated: checkPermission,
};
