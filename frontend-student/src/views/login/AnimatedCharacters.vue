<template>
  <div class="characters-wrapper" ref="wrapper" v-show="!isMobile">
    <div class="characters-scene" ref="scene">
      <!-- Purple character: 170x370, #6c3ff5 -->
      <div class="character char-purple" ref="purple">
        <div class="eyes" ref="purpleEyes" style="left:45px;top:40px;gap:28px">
          <div class="eyeball" ref="purpleEyeL" style="width:18px;height:18px">
            <div class="pupil" ref="purplePupilL" style="width:7px;height:7px"></div>
          </div>
          <div class="eyeball" ref="purpleEyeR" style="width:18px;height:18px">
            <div class="pupil" ref="purplePupilR" style="width:7px;height:7px"></div>
          </div>
        </div>
      </div>
      <!-- Black character: 115x290, #2d2d2d -->
      <div class="character char-black" ref="black">
        <div class="eyes" ref="blackEyes" style="left:26px;top:32px;gap:20px">
          <div class="eyeball" ref="blackEyeL" style="width:16px;height:16px">
            <div class="pupil" ref="blackPupilL" style="width:6px;height:6px"></div>
          </div>
          <div class="eyeball" ref="blackEyeR" style="width:16px;height:16px">
            <div class="pupil" ref="blackPupilR" style="width:6px;height:6px"></div>
          </div>
        </div>
      </div>
      <!-- Orange character: 230x190, #ff9b6b -->
      <div class="character char-orange" ref="orange">
        <div class="eyes" ref="orangeEyes" style="left:82px;top:90px;gap:28px">
          <div class="bare-pupil" ref="orangePupilL"></div>
          <div class="bare-pupil" ref="orangePupilR"></div>
        </div>
        <div class="orange-mouth" ref="orangeMouth" style="left:90px;top:120px"></div>
      </div>
      <!-- Yellow character: 135x215, #e8d754 -->
      <div class="character char-yellow" ref="yellow">
        <div class="eyes" ref="yellowEyes" style="left:52px;top:40px;gap:20px">
          <div class="bare-pupil" ref="yellowPupilL"></div>
          <div class="bare-pupil" ref="yellowPupilR"></div>
        </div>
        <div class="yellow-mouth" ref="yellowMouth" style="left:40px;top:88px"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AnimatedCharacters',
  data () {
    return {
      isMobile: false
    }
  },
  created () {
    // Non-reactive animation state (direct DOM manipulation = 60fps)
    this.mouseX = 0
    this.mouseY = 0
    this.isTyping = false
    this.isLookingAtEachOther = false
    this.isPurpleBlinking = false
    this.isBlackBlinking = false
    this.isPurplePeeking = false
    this.isLoginError = false
    this.isPasswordFocused = false
    this.showPassword = false
    this.hasPassword = false

    this.typingTimer = null
    this.errorRecoverTimer = null
    this.blinkPurpleTimer = null
    this.blinkBlackTimer = null
    this.peekTimer = null
    this.animFrameId = null
  },
  mounted () {
    this.initDOMrefs()
    this.checkMobile()
    this.mq = window.matchMedia('(max-width: 768px)')
    this.mq.addListener(this.onScreenChange)
    this.scheduleBlinkPurple()
    this.scheduleBlinkBlack()
    this.attachMouseMove()
    this.updateCharacters()
  },
  beforeDestroy () {
    // Clean all timers and listeners
    if (this.typingTimer) { clearTimeout(this.typingTimer); this.typingTimer = null }
    if (this.errorRecoverTimer) { clearTimeout(this.errorRecoverTimer); this.errorRecoverTimer = null }
    if (this.blinkPurpleTimer) { clearTimeout(this.blinkPurpleTimer); this.blinkPurpleTimer = null }
    if (this.blinkBlackTimer) { clearTimeout(this.blinkBlackTimer); this.blinkBlackTimer = null }
    if (this.peekTimer) { clearTimeout(this.peekTimer); this.peekTimer = null }
    if (this.animFrameId) { cancelAnimationFrame(this.animFrameId); this.animFrameId = null }
    if (this.mq) { this.mq.removeListener(this.onScreenChange) }
    document.removeEventListener('mousemove', this.onMouseMove)
  },
  methods: {
    /* ---------- Public API (called via $refs from parent) ---------- */
    setTyping (val) {
      this.isTyping = val
      if (val) {
        this.isLookingAtEachOther = true
        if (this.typingTimer) clearTimeout(this.typingTimer)
        this.typingTimer = setTimeout(() => {
          this.isLookingAtEachOther = false
          this.updateCharacters()
        }, 800)
      } else {
        this.isLookingAtEachOther = false
      }
      this.updateCharacters()
    },

    setPasswordFocus (val) {
      this.isPasswordFocused = val
      this.updateCharacters()
    },

    setShowPassword (val) {
      this.showPassword = val
      this.$emit('password-visible', val)
      if (val && this.hasPassword) {
        this.schedulePeek()
      }
      this.updateCharacters()
    },

    setHasPassword (val) {
      this.hasPassword = val
      this.updateCharacters()
    },

    triggerError () {
      if (this.errorRecoverTimer) {
        clearTimeout(this.errorRecoverTimer)
        this.errorRecoverTimer = null
      }

      const shakeIds = [
        this.$refs.purpleEyes,
        this.$refs.blackEyes,
        this.$refs.orangeEyes,
        this.$refs.yellowEyes,
        this.$refs.yellowMouth,
        this.$refs.orangeMouth
      ]
      shakeIds.forEach(el => el.classList.remove('shake-head'))
      // Force reflow
      void this.$refs.scene.offsetHeight

      this.isLoginError = true
      this.updateCharacters()

      // Show sad mouth on orange
      this.$refs.orangeMouth.classList.add('visible')

      // Shake after body transition settles
      setTimeout(() => {
        shakeIds.forEach(el => el.classList.add('shake-head'))
      }, 350)

      this.errorRecoverTimer = setTimeout(() => {
        this.isLoginError = false
        this.$refs.orangeMouth.classList.remove('visible')
        shakeIds.forEach(el => el.classList.remove('shake-head'))
        this.updateCharacters()
        this.errorRecoverTimer = null
      }, 2500)
    },

    /* ---------- Internal ---------- */
    initDOMrefs () {
      this.purple = this.$refs.purple
      this.black = this.$refs.black
      this.orange = this.$refs.orange
      this.yellow = this.$refs.yellow
      this.purpleEyes = this.$refs.purpleEyes
      this.purpleEyeL = this.$refs.purpleEyeL
      this.purpleEyeR = this.$refs.purpleEyeR
      this.purplePupilL = this.$refs.purplePupilL
      this.purplePupilR = this.$refs.purplePupilR
      this.blackEyes = this.$refs.blackEyes
      this.blackEyeL = this.$refs.blackEyeL
      this.blackEyeR = this.$refs.blackEyeR
      this.blackPupilL = this.$refs.blackPupilL
      this.blackPupilR = this.$refs.blackPupilR
      this.orangeEyes = this.$refs.orangeEyes
      this.orangePupilL = this.$refs.orangePupilL
      this.orangePupilR = this.$refs.orangePupilR
      this.orangeMouth = this.$refs.orangeMouth
      this.yellowEyes = this.$refs.yellowEyes
      this.yellowPupilL = this.$refs.yellowPupilL
      this.yellowPupilR = this.$refs.yellowPupilR
      this.yellowMouth = this.$refs.yellowMouth
      this.scene = this.$refs.scene
    },

    checkMobile () {
      this.isMobile = window.innerWidth <= 768
    },

    onScreenChange (e) {
      this.isMobile = e.matches
    },

    attachMouseMove () {
      // requestAnimationFrame throttle for 60fps
      this.onMouseMove = (e) => {
        this.mouseX = e.clientX
        this.mouseY = e.clientY
        if (!this.isTyping && !this.isLoginError) {
          if (this.animFrameId) cancelAnimationFrame(this.animFrameId)
          this.animFrameId = requestAnimationFrame(() => {
            this.updateCharacters()
            this.animFrameId = null
          })
        }
      }
      document.addEventListener('mousemove', this.onMouseMove)
    },

    scheduleBlinkPurple () {
      this.blinkPurpleTimer = setTimeout(() => {
        this.isPurpleBlinking = true
        this.updateCharacters()
        setTimeout(() => {
          this.isPurpleBlinking = false
          this.updateCharacters()
          this.scheduleBlinkPurple()
        }, 150)
      }, Math.random() * 4000 + 3000)
    },

    scheduleBlinkBlack () {
      this.blinkBlackTimer = setTimeout(() => {
        this.isBlackBlinking = true
        this.updateCharacters()
        setTimeout(() => {
          this.isBlackBlinking = false
          this.updateCharacters()
          this.scheduleBlinkBlack()
        }, 150)
      }, Math.random() * 4000 + 3000)
    },

    schedulePeek () {
      if (this.peekTimer) clearTimeout(this.peekTimer)
      if (!this.hasPassword || !this.showPassword) return
      this.peekTimer = setTimeout(() => {
        if (this.hasPassword && this.showPassword) {
          this.isPurplePeeking = true
          this.updateCharacters()
          setTimeout(() => {
            this.isPurplePeeking = false
            this.updateCharacters()
            this.schedulePeek()
          }, 800)
        }
      }, Math.random() * 3000 + 2000)
    },

    calcPosition (el) {
      const rect = el.getBoundingClientRect()
      const cx = rect.left + rect.width / 2
      const cy = rect.top + rect.height / 3
      const dx = this.mouseX - cx
      const dy = this.mouseY - cy
      const faceX = Math.max(-15, Math.min(15, dx / 20))
      const faceY = Math.max(-10, Math.min(10, dy / 30))
      const bodySkew = Math.max(-6, Math.min(6, -dx / 120))
      return { faceX, faceY, bodySkew }
    },

    calcPupilOffset (el, maxDist) {
      const rect = el.getBoundingClientRect()
      const cx = rect.left + rect.width / 2
      const cy = rect.top + rect.height / 2
      const dx = this.mouseX - cx
      const dy = this.mouseY - cy
      const dist = Math.min(Math.sqrt(dx * dx + dy * dy), maxDist)
      const angle = Math.atan2(dy, dx)
      return { x: Math.cos(angle) * dist, y: Math.sin(angle) * dist }
    },

    updateCharacters () {
      const purplePos = this.calcPosition(this.purple)
      const blackPos = this.calcPosition(this.black)
      const orangePos = this.calcPosition(this.orange)
      const yellowPos = this.calcPosition(this.yellow)

      const isShowingPwd = this.hasPassword && this.showPassword
      const isLookingAway = this.isPasswordFocused && !this.showPassword

      // ---- Purple body ----
      if (isShowingPwd) {
        this.purple.style.transform = 'skewX(0deg)'
        this.purple.style.height = '370px'
      } else if (isLookingAway) {
        this.purple.style.transform = 'skewX(-14deg) translateX(-20px)'
        this.purple.style.height = '410px'
      } else if (this.isTyping) {
        this.purple.style.transform = `skewX(${(purplePos.bodySkew || 0) - 12}deg) translateX(40px)`
        this.purple.style.height = '410px'
      } else {
        this.purple.style.transform = `skewX(${purplePos.bodySkew}deg)`
        this.purple.style.height = '370px'
      }

      // Purple eyes
      this.purpleEyes.style.transition = ''
      this.purpleEyeL.style.height = this.isPurpleBlinking ? '2px' : '18px'
      this.purpleEyeR.style.height = this.isPurpleBlinking ? '2px' : '18px'

      if (this.isLoginError) {
        this.purpleEyes.style.left = '30px'
        this.purpleEyes.style.top = '55px'
        this.purplePupilL.style.transform = 'translate(-3px, 4px)'
        this.purplePupilR.style.transform = 'translate(-3px, 4px)'
      } else if (isLookingAway) {
        this.purpleEyes.style.left = '20px'
        this.purpleEyes.style.top = '25px'
        this.purplePupilL.style.transform = 'translate(-5px, -5px)'
        this.purplePupilR.style.transform = 'translate(-5px, -5px)'
      } else if (isShowingPwd) {
        this.purpleEyes.style.left = '20px'
        this.purpleEyes.style.top = '35px'
        const px = this.isPurplePeeking ? 4 : -4
        const py = this.isPurplePeeking ? 5 : -4
        this.purplePupilL.style.transform = `translate(${px}px, ${py}px)`
        this.purplePupilR.style.transform = `translate(${px}px, ${py}px)`
  } else if (this.isLookingAtEachOther) {
    this.purpleEyes.style.left = '55px'
    this.purpleEyes.style.top = '50px'
        this.purplePupilL.style.transform = 'translate(3px, 4px)'
        this.purplePupilR.style.transform = 'translate(3px, 4px)'
      } else {
        this.purpleEyes.style.transition = 'left 0.25s ease-out, top 0.25s ease-out'
        this.purpleEyes.style.left = 45 + purplePos.faceX + 'px'
        this.purpleEyes.style.top = 40 + purplePos.faceY + 'px'
        const po = this.calcPupilOffset(this.purpleEyeL, 5)
        this.purplePupilL.style.transform = `translate(${po.x}px, ${po.y}px)`
        this.purplePupilR.style.transform = `translate(${po.x}px, ${po.y}px)`
      }

      // ---- Black body ----
      if (isShowingPwd) {
        this.black.style.transform = 'skewX(0deg)'
      } else if (isLookingAway) {
        this.black.style.transform = 'skewX(12deg) translateX(-10px)'
      } else if (this.isLookingAtEachOther) {
        this.black.style.transform = `skewX(${(blackPos.bodySkew || 0) * 1.5 + 10}deg) translateX(20px)`
      } else if (this.isTyping) {
        this.black.style.transform = `skewX(${(blackPos.bodySkew || 0) * 1.5}deg)`
      } else {
        this.black.style.transform = `skewX(${blackPos.bodySkew}deg)`
      }

      // Black eyes
      this.blackEyes.style.transition = ''
      this.blackEyeL.style.height = this.isBlackBlinking ? '2px' : '16px'
      this.blackEyeR.style.height = this.isBlackBlinking ? '2px' : '16px'

      if (this.isLoginError) {
        this.blackEyes.style.left = '15px'
        this.blackEyes.style.top = '40px'
        this.blackPupilL.style.transform = 'translate(-3px, 4px)'
        this.blackPupilR.style.transform = 'translate(-3px, 4px)'
      } else if (isLookingAway) {
        this.blackEyes.style.left = '10px'
        this.blackEyes.style.top = '20px'
        this.blackPupilL.style.transform = 'translate(-4px, -5px)'
        this.blackPupilR.style.transform = 'translate(-4px, -5px)'
      } else if (isShowingPwd) {
        this.blackEyes.style.left = '10px'
        this.blackEyes.style.top = '28px'
        this.blackPupilL.style.transform = 'translate(-4px, -4px)'
        this.blackPupilR.style.transform = 'translate(-4px, -4px)'
  } else if (this.isLookingAtEachOther) {
    this.blackEyes.style.left = '16px'
    this.blackEyes.style.top = '22px'
        this.blackPupilL.style.transform = 'translate(0px, -4px)'
        this.blackPupilR.style.transform = 'translate(0px, -4px)'
      } else {
        this.blackEyes.style.transition = 'left 0.25s ease-out, top 0.25s ease-out'
        this.blackEyes.style.left = 26 + blackPos.faceX + 'px'
        this.blackEyes.style.top = 32 + blackPos.faceY + 'px'
        const bo = this.calcPupilOffset(this.blackEyeL, 4)
        this.blackPupilL.style.transform = `translate(${bo.x}px, ${bo.y}px)`
        this.blackPupilR.style.transform = `translate(${bo.x}px, ${bo.y}px)`
      }

      // ---- Orange body ----
      if (this.isLoginError) {
        this.orangeMouth.style.left = 80 + orangePos.faceX + 'px'
        this.orangeMouth.style.top = '130px'
      }
      if (isShowingPwd) {
        this.orange.style.transform = 'skewX(0deg)'
      } else {
        this.orange.style.transform = `skewX(${orangePos.bodySkew}deg)`
      }

      // Orange eyes
      this.orangeEyes.style.transition = ''
      if (this.isLoginError) {
        this.orangeEyes.style.left = '60px'
        this.orangeEyes.style.top = '95px'
        this.orangePupilL.style.transform = 'translate(-3px, 4px)'
        this.orangePupilR.style.transform = 'translate(-3px, 4px)'
      } else if (isLookingAway) {
        this.orangeEyes.style.left = '50px'
        this.orangeEyes.style.top = '75px'
        this.orangePupilL.style.transform = 'translate(-5px, -5px)'
        this.orangePupilR.style.transform = 'translate(-5px, -5px)'
      } else if (isShowingPwd) {
        this.orangeEyes.style.left = '50px'
        this.orangeEyes.style.top = '85px'
        this.orangePupilL.style.transform = 'translate(-5px, -4px)'
        this.orangePupilR.style.transform = 'translate(-5px, -4px)'
      } else {
        this.orangeEyes.style.transition = 'left 0.25s ease-out, top 0.25s ease-out'
        this.orangeEyes.style.left = 82 + orangePos.faceX + 'px'
        this.orangeEyes.style.top = 90 + orangePos.faceY + 'px'
        const oo = this.calcPupilOffset(this.orangePupilL, 5)
        this.orangePupilL.style.transform = `translate(${oo.x}px, ${oo.y}px)`
        this.orangePupilR.style.transform = `translate(${oo.x}px, ${oo.y}px)`
      }

      // ---- Yellow body ----
      if (isShowingPwd) {
        this.yellow.style.transform = 'skewX(0deg)'
      } else {
        this.yellow.style.transform = `skewX(${yellowPos.bodySkew}deg)`
      }

      // Yellow eyes & mouth
      this.yellowEyes.style.transition = ''
      if (this.isLoginError) {
        this.yellowEyes.style.left = '35px'
        this.yellowEyes.style.top = '45px'
        this.yellowPupilL.style.transform = 'translate(-3px, 4px)'
        this.yellowPupilR.style.transform = 'translate(-3px, 4px)'
        this.yellowMouth.style.left = '30px'
        this.yellowMouth.style.top = '92px'
        this.yellowMouth.style.transform = 'rotate(-8deg)'
      } else if (isLookingAway) {
        this.yellowEyes.style.left = '20px'
        this.yellowEyes.style.top = '30px'
        this.yellowPupilL.style.transform = 'translate(-5px, -5px)'
        this.yellowPupilR.style.transform = 'translate(-5px, -5px)'
        this.yellowMouth.style.left = '15px'
        this.yellowMouth.style.top = '78px'
        this.yellowMouth.style.transform = 'rotate(0deg)'
      } else if (isShowingPwd) {
        this.yellowEyes.style.left = '20px'
        this.yellowEyes.style.top = '35px'
        this.yellowPupilL.style.transform = 'translate(-5px, -4px)'
        this.yellowPupilR.style.transform = 'translate(-5px, -4px)'
        this.yellowMouth.style.left = '10px'
        this.yellowMouth.style.top = '88px'
        this.yellowMouth.style.transform = 'rotate(0deg)'
      } else {
        this.yellowEyes.style.transition = 'left 0.25s ease-out, top 0.25s ease-out'
        this.yellowEyes.style.left = 52 + yellowPos.faceX + 'px'
        this.yellowEyes.style.top = 40 + yellowPos.faceY + 'px'
        const yo = this.calcPupilOffset(this.yellowPupilL, 5)
        this.yellowPupilL.style.transform = `translate(${yo.x}px, ${yo.y}px)`
        this.yellowPupilR.style.transform = `translate(${yo.x}px, ${yo.y}px)`
        this.yellowMouth.style.left = 40 + yellowPos.faceX + 'px'
        this.yellowMouth.style.top = 88 + yellowPos.faceY + 'px'
        this.yellowMouth.style.transform = 'rotate(0deg)'
      }
    }
  }
}
</script>

<style scoped>
.characters-wrapper {
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.characters-scene {
  position: relative;
  width: 480px;
  height: 360px;
}

.character {
  position: absolute;
  bottom: 0;
  transition: all 0.7s ease-in-out;
  transform-origin: bottom center;
}

/* Purple rectangle */
.char-purple {
  left: 60px;
  width: 170px;
  height: 370px;
  background: #6c3ff5;
  border-radius: 10px 10px 0 0;
  z-index: 1;
}

/* Black rectangle */
.char-black {
  left: 220px;
  width: 115px;
  height: 290px;
  background: #2d2d2d;
  border-radius: 8px 8px 0 0;
  z-index: 2;
}

/* Orange semi-circle */
.char-orange {
  left: 0;
  width: 230px;
  height: 190px;
  background: #ff9b6b;
  border-radius: 115px 115px 0 0;
  z-index: 3;
}

/* Yellow rounded rectangle */
.char-yellow {
  left: 290px;
  width: 135px;
  height: 215px;
  background: #e8d754;
  border-radius: 68px 68px 0 0;
  z-index: 4;
}

/* Eyes container */
.eyes {
  position: absolute;
  display: flex;
  transition: all 0.7s ease-in-out;
}

/* Eyeball (white with pupil) */
.eyeball {
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: height 0.15s ease;
  overflow: hidden;
}

/* Pupil inside eyeball */
.pupil {
  border-radius: 50%;
  background: #2d2d2d;
  transition: transform 0.1s ease-out;
}

/* Bare pupil (no white eyeball - for orange & yellow) */
.bare-pupil {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2d2d2d;
  transition: transform 0.1s ease-out;
}

/* Yellow mouth (4px high line) */
.yellow-mouth {
  position: absolute;
  width: 50px;
  height: 4px;
  background: #2d2d2d;
  border-radius: 2px;
  transition: all 0.7s ease-in-out;
}

/* Sad mouth for orange character */
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

/* Shake head animation */
@keyframes shakeHead {
  0%, 100% { translate: 0 0; }
  10% { translate: -9px 0; }
  20% { translate: 7px 0; }
  30% { translate: -6px 0; }
  40% { translate: 5px 0; }
  50% { translate: -4px 0; }
  60% { translate: 3px 0; }
  70% { translate: -2px 0; }
  80% { translate: 1px 0; }
  90% { translate: -0.5px 0; }
}

.eyes.shake-head,
.yellow-mouth.shake-head,
.orange-mouth.shake-head {
  animation: shakeHead 0.8s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
</style>
