<template>
  <div class="characters-scene" id="characters-scene">
    <!-- 紫色角色 -->
    <div class="character char-purple" id="char-purple">
      <div class="eyes" id="purple-eyes" style="left:45px;top:40px;gap:28px">
        <div class="eyeball" id="purple-eye-l" style="width:18px;height:18px">
          <div class="pupil" id="purple-pupil-l" style="width:7px;height:7px"></div>
        </div>
        <div class="eyeball" id="purple-eye-r" style="width:18px;height:18px">
          <div class="pupil" id="purple-pupil-r" style="width:7px;height:7px"></div>
        </div>
      </div>
    </div>
    <!-- 黑色角色 -->
    <div class="character char-black" id="char-black">
      <div class="eyes" id="black-eyes" style="left:26px;top:32px;gap:20px">
        <div class="eyeball" id="black-eye-l" style="width:16px;height:16px">
          <div class="pupil" id="black-pupil-l" style="width:6px;height:6px"></div>
        </div>
        <div class="eyeball" id="black-eye-r" style="width:16px;height:16px">
          <div class="pupil" id="black-pupil-r" style="width:6px;height:6px"></div>
        </div>
      </div>
    </div>
    <!-- 橙色角色 -->
    <div class="character char-orange" id="char-orange">
      <div class="eyes" id="orange-eyes" style="left:82px;top:90px;gap:28px">
        <div class="bare-pupil" id="orange-pupil-l"></div>
        <div class="bare-pupil" id="orange-pupil-r"></div>
      </div>
      <div class="orange-mouth" id="orange-mouth" style="left:90px;top:120px"></div>
    </div>
    <!-- 黄色角色 -->
    <div class="character char-yellow" id="char-yellow">
      <div class="eyes" id="yellow-eyes" style="left:52px;top:40px;gap:20px">
        <div class="bare-pupil" id="yellow-pupil-l"></div>
        <div class="bare-pupil" id="yellow-pupil-r"></div>
      </div>
      <div class="yellow-mouth" id="yellow-mouth" style="left:40px;top:88px"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { watch, onMounted, onUnmounted } from 'vue'

const props = withDefaults(defineProps<{
  /** 用户名输入框是否聚焦 */
  usernameFocus?: boolean
  /** 密码输入框是否聚焦 */
  passwordFocus?: boolean
  /** 密码是否可见 */
  passwordVisible?: boolean
  /** 是否触发登录错误 */
  loginError?: boolean
}>(), {
  usernameFocus: false,
  passwordFocus: false,
  passwordVisible: false,
  loginError: false,
})

const emit = defineEmits<{
  (e: 'password-visible', value: boolean): void
  (e: 'trigger-error'): void
}>()

// ---- 状态变量 ----
let mouseX = 0
let mouseY = 0
let isPurpleBlinking = false
let isBlackBlinking = false
let isPurplePeeking = false
let isLoginError = false
let isTyping = false
let isLookingAtEachOther = false
let rafId: number | null = null
let blinkPurpleTimer: ReturnType<typeof setTimeout> | null = null
let blinkBlackTimer: ReturnType<typeof setTimeout> | null = null
let peekTimer: ReturnType<typeof setTimeout> | null = null
let typingTimer: ReturnType<typeof setTimeout> | null = null
let errorRecoverTimer: ReturnType<typeof setTimeout> | null = null

// ---- 遮罩节点集合（触发摇头动画的元素） ----
const shakeIds = [
  'purple-eyes',
  'black-eyes',
  'orange-eyes',
  'yellow-eyes',
  'yellow-mouth',
  'orange-mouth',
]

// ---- DOM 辅助 ----
function $(id: string): HTMLElement | null {
  return document.getElementById(id)
}

// ---- 鼠标追踪 (RAF 节流) ----
function onMouseMove(e: MouseEvent) {
  mouseX = e.clientX
  mouseY = e.clientY
  if (!isTyping && !isLoginError) {
    if (rafId) cancelAnimationFrame(rafId)
    rafId = requestAnimationFrame(() => {
      updateCharacters()
      rafId = null
    })
  }
}

// ---- 位置计算 ----
function calcPosition(el: HTMLElement | null) {
  if (!el) return { faceX: 0, faceY: 0, bodySkew: 0 }
  const rect = el.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 3
  const dx = mouseX - cx
  const dy = mouseY - cy
  const faceX = Math.max(-15, Math.min(15, dx / 20))
  const faceY = Math.max(-10, Math.min(10, dy / 30))
  const bodySkew = Math.max(-6, Math.min(6, -dx / 120))
  return { faceX, faceY, bodySkew }
}

function calcPupilOffset(el: HTMLElement | null, maxDist: number) {
  if (!el) return { x: 0, y: 0 }
  const rect = el.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2
  const dx = mouseX - cx
  const dy = mouseY - cy
  const dist = Math.min(Math.sqrt(dx * dx + dy * dy), maxDist)
  const angle = Math.atan2(dy, dx)
  return { x: Math.cos(angle) * dist, y: Math.sin(angle) * dist }
}

// ---- 主更新函数 (直接操作 DOM style，不走 Vue 响应式) ----
function updateCharacters() {
  const purple = $('char-purple')
  const black = $('char-black')
  const orange = $('char-orange')
  const yellow = $('char-yellow')

  // DOM 未就绪时跳过
  if (!purple || !black || !orange || !yellow) return

  const purplePos = calcPosition(purple)
  const blackPos = calcPosition(black)
  const orangePos = calcPosition(orange)
  const yellowPos = calcPosition(yellow)

  const isShowingPwd = props.passwordVisible
  const isLookingAway = props.passwordFocus && !props.passwordVisible

  // ── 紫色角色 ──
  if (isShowingPwd) {
    purple.style.transform = 'skewX(0deg)'
    purple.style.height = '370px'
  } else if (isLookingAway) {
    purple.style.transform = 'skewX(-14deg) translateX(-20px)'
    purple.style.height = '410px'
  } else if (isTyping) {
    purple.style.transform = `skewX(${(purplePos.bodySkew || 0) - 12}deg) translateX(40px)`
    purple.style.height = '410px'
  } else {
    purple.style.transform = `skewX(${purplePos.bodySkew}deg)`
    purple.style.height = '370px'
  }

  // 紫色眼睛
  const purpleEyes = $('purple-eyes')
  const purpleEyeL = $('purple-eye-l')
  const purpleEyeR = $('purple-eye-r')
  const purplePupilL = $('purple-pupil-l')
  const purplePupilR = $('purple-pupil-r')

      purpleEyeL.style.height = isPurpleBlinking ? '2px' : '18px'
      purpleEyeR.style.height = isPurpleBlinking ? '2px' : '18px'

      purpleEyes.style.transition = (isLoginError || isLookingAway || isShowingPwd || isLookingAtEachOther) ? '' : 'left 0.25s ease-out, top 0.25s ease-out'

      if (isLoginError) {
    purpleEyes.style.left = '30px'
    purpleEyes.style.top = '55px'
    purplePupilL.style.transform = 'translate(-3px, 4px)'
    purplePupilR.style.transform = 'translate(-3px, 4px)'
  } else if (isLookingAway) {
    purpleEyes.style.left = '20px'
    purpleEyes.style.top = '25px'
    purplePupilL.style.transform = 'translate(-5px, -5px)'
    purplePupilR.style.transform = 'translate(-5px, -5px)'
  } else if (isShowingPwd) {
    purpleEyes.style.left = '20px'
    purpleEyes.style.top = '35px'
    const px = isPurplePeeking ? 4 : -4
    const py = isPurplePeeking ? 5 : -4
    purplePupilL.style.transform = `translate(${px}px, ${py}px)`
    purplePupilR.style.transform = `translate(${px}px, ${py}px)`
  } else if (isLookingAtEachOther) {
    purpleEyes.style.left = '55px'
    purpleEyes.style.top = '50px'
    purplePupilL.style.transform = 'translate(3px, 4px)'
    purplePupilR.style.transform = 'translate(3px, 4px)'
  } else {
    purpleEyes.style.left = 45 + purplePos.faceX + 'px'
    purpleEyes.style.top = 40 + purplePos.faceY + 'px'
    const po = calcPupilOffset(purpleEyeL, 5)
    purplePupilL.style.transform = `translate(${po.x}px, ${po.y}px)`
    purplePupilR.style.transform = `translate(${po.x}px, ${po.y}px)`
  }

  // ── 黑色角色 ──
  if (isShowingPwd) {
    black.style.transform = 'skewX(0deg)'
  } else if (isLookingAway) {
    black.style.transform = 'skewX(12deg) translateX(-10px)'
  } else if (isLookingAtEachOther) {
    black.style.transform = `skewX(${(blackPos.bodySkew || 0) * 1.5 + 10}deg) translateX(20px)`
  } else if (isTyping) {
    black.style.transform = `skewX(${(blackPos.bodySkew || 0) * 1.5}deg)`
  } else {
    black.style.transform = `skewX(${blackPos.bodySkew}deg)`
  }

  // 黑色眼睛
  const blackEyes = $('black-eyes')
  const blackEyeL = $('black-eye-l')
  const blackEyeR = $('black-eye-r')
  const blackPupilL = $('black-pupil-l')
  const blackPupilR = $('black-pupil-r')

      blackEyeL.style.height = isBlackBlinking ? '2px' : '16px'
      blackEyeR.style.height = isBlackBlinking ? '2px' : '16px'

      blackEyes.style.transition = (isLoginError || isLookingAway || isShowingPwd || isLookingAtEachOther) ? '' : 'left 0.25s ease-out, top 0.25s ease-out'

      if (isLoginError) {
    blackEyes.style.left = '15px'
    blackEyes.style.top = '40px'
    blackPupilL.style.transform = 'translate(-3px, 4px)'
    blackPupilR.style.transform = 'translate(-3px, 4px)'
  } else if (isLookingAway) {
    blackEyes.style.left = '10px'
    blackEyes.style.top = '20px'
    blackPupilL.style.transform = 'translate(-4px, -5px)'
    blackPupilR.style.transform = 'translate(-4px, -5px)'
  } else if (isShowingPwd) {
    blackEyes.style.left = '10px'
    blackEyes.style.top = '28px'
    blackPupilL.style.transform = 'translate(-4px, -4px)'
    blackPupilR.style.transform = 'translate(-4px, -4px)'
  } else if (isLookingAtEachOther) {
    blackEyes.style.left = '16px'
    blackEyes.style.top = '22px'
    blackPupilL.style.transform = 'translate(0px, -4px)'
    blackPupilR.style.transform = 'translate(0px, -4px)'
  } else {
    blackEyes.style.left = 26 + blackPos.faceX + 'px'
    blackEyes.style.top = 32 + blackPos.faceY + 'px'
    const bo = calcPupilOffset(blackEyeL, 4)
    blackPupilL.style.transform = `translate(${bo.x}px, ${bo.y}px)`
    blackPupilR.style.transform = `translate(${bo.x}px, ${bo.y}px)`
  }

  // ── 橙色角色 ──
  const orangeMouth = $('orange-mouth')
  if (isLoginError) {
    orangeMouth.style.left = 80 + orangePos.faceX + 'px'
    orangeMouth.style.top = '130px'
  }
  if (isShowingPwd) {
    orange.style.transform = 'skewX(0deg)'
  } else {
    orange.style.transform = `skewX(${orangePos.bodySkew}deg)`
  }

      // 橙色眼睛 (裸瞳孔)
      const orangeEyes = $('orange-eyes')
      const orangePupilL = $('orange-pupil-l')
      const orangePupilR = $('orange-pupil-r')

      orangeEyes.style.transition = (isLoginError || isLookingAway || isShowingPwd) ? '' : 'left 0.25s ease-out, top 0.25s ease-out'

      if (isLoginError) {
    orangeEyes.style.left = '60px'
    orangeEyes.style.top = '95px'
    orangePupilL.style.transform = 'translate(-3px, 4px)'
    orangePupilR.style.transform = 'translate(-3px, 4px)'
  } else if (isLookingAway) {
    orangeEyes.style.left = '50px'
    orangeEyes.style.top = '75px'
    orangePupilL.style.transform = 'translate(-5px, -5px)'
    orangePupilR.style.transform = 'translate(-5px, -5px)'
  } else if (isShowingPwd) {
    orangeEyes.style.left = '50px'
    orangeEyes.style.top = '85px'
    orangePupilL.style.transform = 'translate(-5px, -4px)'
    orangePupilR.style.transform = 'translate(-5px, -4px)'
  } else {
    orangeEyes.style.left = 82 + orangePos.faceX + 'px'
    orangeEyes.style.top = 90 + orangePos.faceY + 'px'
    const oo = calcPupilOffset(orangePupilL, 5)
    orangePupilL.style.transform = `translate(${oo.x}px, ${oo.y}px)`
    orangePupilR.style.transform = `translate(${oo.x}px, ${oo.y}px)`
  }

  // ── 黄色角色 ──
  if (isShowingPwd) {
    yellow.style.transform = 'skewX(0deg)'
  } else {
    yellow.style.transform = `skewX(${yellowPos.bodySkew}deg)`
  }

      // 黄色眼睛 & 嘴巴
      const yellowEyes = $('yellow-eyes')
      const yellowPupilL = $('yellow-pupil-l')
      const yellowPupilR = $('yellow-pupil-r')
      const yellowMouth = $('yellow-mouth')

      yellowEyes.style.transition = (isLoginError || isLookingAway || isShowingPwd) ? '' : 'left 0.25s ease-out, top 0.25s ease-out'

      if (isLoginError) {
    yellowEyes.style.left = '35px'
    yellowEyes.style.top = '45px'
    yellowPupilL.style.transform = 'translate(-3px, 4px)'
    yellowPupilR.style.transform = 'translate(-3px, 4px)'
    yellowMouth.style.left = '30px'
    yellowMouth.style.top = '92px'
    yellowMouth.style.transform = 'rotate(-8deg)'
  } else if (isLookingAway) {
    yellowEyes.style.left = '20px'
    yellowEyes.style.top = '30px'
    yellowPupilL.style.transform = 'translate(-5px, -5px)'
    yellowPupilR.style.transform = 'translate(-5px, -5px)'
    yellowMouth.style.left = '15px'
    yellowMouth.style.top = '78px'
    yellowMouth.style.transform = 'rotate(0deg)'
  } else if (isShowingPwd) {
    yellowEyes.style.left = '20px'
    yellowEyes.style.top = '35px'
    yellowPupilL.style.transform = 'translate(-5px, -4px)'
    yellowPupilR.style.transform = 'translate(-5px, -4px)'
    yellowMouth.style.left = '10px'
    yellowMouth.style.top = '88px'
    yellowMouth.style.transform = 'rotate(0deg)'
  } else {
    yellowEyes.style.left = 52 + yellowPos.faceX + 'px'
    yellowEyes.style.top = 40 + yellowPos.faceY + 'px'
    const yo = calcPupilOffset(yellowPupilL, 5)
    yellowPupilL.style.transform = `translate(${yo.x}px, ${yo.y}px)`
    yellowPupilR.style.transform = `translate(${yo.x}px, ${yo.y}px)`
    yellowMouth.style.left = 40 + yellowPos.faceX + 'px'
    yellowMouth.style.top = 88 + yellowPos.faceY + 'px'
    yellowMouth.style.transform = 'rotate(0deg)'
  }
}

// ---- 眨眼定时器 (紫色 & 黑色，3-7s 随机间隔，持续 150ms) ----
function scheduleBlinkPurple() {
  blinkPurpleTimer = setTimeout(() => {
    isPurpleBlinking = true
    updateCharacters()
    setTimeout(() => {
      isPurpleBlinking = false
      updateCharacters()
      scheduleBlinkPurple()
    }, 150)
  }, Math.random() * 4000 + 3000)
}

function scheduleBlinkBlack() {
  blinkBlackTimer = setTimeout(() => {
    isBlackBlinking = true
    updateCharacters()
    setTimeout(() => {
      isBlackBlinking = false
      updateCharacters()
      scheduleBlinkBlack()
    }, 150)
  }, Math.random() * 4000 + 3000)
}

// ---- 紫色偷看 (密码可见时 2-5s 间隔，持续 800ms) ----
function schedulePeek() {
  if (peekTimer) {
    clearTimeout(peekTimer)
    peekTimer = null
  }
  if (!props.passwordVisible) return

  peekTimer = setTimeout(() => {
    if (!props.passwordVisible) return

    isPurplePeeking = true
    emit('password-visible', true)
    updateCharacters()

    peekTimer = setTimeout(() => {
      isPurplePeeking = false
      emit('password-visible', false)
      updateCharacters()
      schedulePeek()
    }, 800)
  }, Math.random() * 3000 + 2000)
}

// ---- 登录错误动画 (沮丧表情 + 摇头 + 2.5s 后恢复) ----
function triggerLoginError() {
  if (errorRecoverTimer) {
    clearTimeout(errorRecoverTimer)
    errorRecoverTimer = null
  }

  // 重置摇头动画 (移除 class + 强制回流以允许重复触发)
  const shakeEls = shakeIds.map(id => $(id))
  shakeEls.forEach(el => el.classList.remove('shake-head'))
  // eslint-disable-next-line @typescript-eslint/no-unused-expressions
  void document.body.offsetHeight

  isLoginError = true
  emit('trigger-error')
  updateCharacters()

  // 显示橙色的悲伤嘴巴
  $('orange-mouth').classList.add('visible')

  // 350ms 后启动摇头
  setTimeout(() => {
    shakeEls.forEach(el => el.classList.add('shake-head'))
  }, 350)

  // 2.5s 后恢复
  errorRecoverTimer = setTimeout(() => {
    isLoginError = false
    errorRecoverTimer = null
    $('orange-mouth').classList.remove('visible')
    shakeEls.forEach(el => el.classList.remove('shake-head'))
    updateCharacters()
  }, 2500)
}

// ---- Props 监听 ----
watch(() => props.usernameFocus, (val: boolean) => {
  if (val) {
    isTyping = true
    isLookingAtEachOther = true
    clearTimeout(typingTimer!)
    typingTimer = setTimeout(() => {
      isLookingAtEachOther = false
      updateCharacters()
    }, 800)
  } else {
    isTyping = false
    isLookingAtEachOther = false
  }
  updateCharacters()
})

watch(() => props.passwordFocus, () => {
  updateCharacters()
})

watch(() => props.passwordVisible, (val: boolean) => {
  if (peekTimer) {
    clearTimeout(peekTimer)
    peekTimer = null
  }
  updateCharacters()
  if (val) schedulePeek()
})

watch(() => props.loginError, (val: boolean) => {
  if (val) triggerLoginError()
})

// ---- 生命周期 ----
onMounted(() => {
  document.addEventListener('mousemove', onMouseMove)
  scheduleBlinkPurple()
  scheduleBlinkBlack()
  updateCharacters()
})

onUnmounted(() => {
  document.removeEventListener('mousemove', onMouseMove)
  if (rafId) cancelAnimationFrame(rafId)
  if (blinkPurpleTimer) clearTimeout(blinkPurpleTimer)
  if (blinkBlackTimer) clearTimeout(blinkBlackTimer)
  if (peekTimer) clearTimeout(peekTimer)
  if (typingTimer) clearTimeout(typingTimer)
  if (errorRecoverTimer) clearTimeout(errorRecoverTimer)
})
</script>

<style scoped>
/* ===== 场景容器 ===== */
.characters-scene {
  position: relative;
  width: 480px;
  height: 360px;
}

/* ===== 角色基础样式 ===== */
.character {
  position: absolute;
  bottom: 0;
  transition: all 0.7s ease-in-out;
  transform-origin: bottom center;
}

/* 紫色矩形 */
.char-purple {
  left: 60px;
  width: 170px;
  height: 370px;
  background: #6c3ff5;
  border-radius: 10px 10px 0 0;
  z-index: 1;
}

/* 黑色矩形 */
.char-black {
  left: 220px;
  width: 115px;
  height: 290px;
  background: #2d2d2d;
  border-radius: 8px 8px 0 0;
  z-index: 2;
}

/* 橙色半圆 */
.char-orange {
  left: 0;
  width: 230px;
  height: 190px;
  background: #ff9b6b;
  border-radius: 115px 115px 0 0;
  z-index: 3;
}

/* 黄色圆角矩形 */
.char-yellow {
  left: 290px;
  width: 135px;
  height: 215px;
  background: #e8d754;
  border-radius: 68px 68px 0 0;
  z-index: 4;
}

/* ===== 眼睛 ===== */
.eyes {
  position: absolute;
  display: flex;
  transition: all 0.7s ease-in-out;
}

/* 眼球 (白色 + 瞳孔) */
.eyeball {
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: height 0.15s ease;
  overflow: hidden;
}

/* 瞳孔 (深色) */
.pupil {
  border-radius: 50%;
  background: #2d2d2d;
  transition: transform 0.1s ease-out;
}

/* 裸瞳孔 (无白色眼球) */
.bare-pupil {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2d2d2d;
  transition: transform 0.1s ease-out;
}

/* ===== 嘴巴 ===== */
/* 黄色嘴巴 (4px 横线) */
.yellow-mouth {
  position: absolute;
  width: 50px;
  height: 4px;
  background: #2d2d2d;
  border-radius: 2px;
  transition: all 0.7s ease-in-out;
}

/* 橙色悲伤嘴巴 (弯曲) */
.orange-mouth {
  position: absolute;
  width: 28px;
  height: 14px;
  border: 3px solid #2d2d2d;
  border-top: none;
  border-radius: 0 0 14px 14px;
  opacity: 0;
  transition: all 0.7s ease-in-out;
}

.orange-mouth.visible {
  opacity: 1;
}

/* ===== 摇头动画 ===== */
@keyframes shakeHead {
  0%, 100% { transform: translateX(0); }
  10% { transform: translateX(-9px); }
  20% { transform: translateX(7px); }
  30% { transform: translateX(-6px); }
  40% { transform: translateX(5px); }
  50% { transform: translateX(-4px); }
  60% { transform: translateX(3px); }
  70% { transform: translateX(-2px); }
  80% { transform: translateX(1px); }
  90% { transform: translateX(-0.5px); }
}

.eyes.shake-head,
.yellow-mouth.shake-head,
.orange-mouth.shake-head {
  animation: shakeHead 0.8s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

/* ===== 响应式: ≤768px 隐藏角色场景 ===== */
@media (max-width: 768px) {
  .characters-scene {
    display: none;
  }
}
</style>
