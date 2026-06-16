<template>
  <div style="margin-top: 10px" class="app-contain">
    <el-row :gutter="50">
      <el-col :span="7">
        <el-card>
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <el-row style="text-align: center">
            <el-upload action="/api/user/avatar" accept=".jpg,.png" :show-file-list="false" :on-success="uploadSuccess" :headers="uploadHeaders">
              <el-avatar class="el-dropdown-avatar" :size="100" :src="(form.avatar === null || form.avatar === undefined) ? require('@/assets/avatar.png') : form.avatar"></el-avatar>
            </el-upload>
          </el-row>
          <el-row class="user-info-userName">
            <label>{{ form.username }}</label>
          </el-row>
          <el-divider/>
          <el-row class="user-info-fullInfo">
            <label>姓名：{{ form.realName }}</label><br/>
            <label>角色：{{ roleText(form.role) }}</label><br/>
            <label v-if="form.studentNo">学号：{{ form.studentNo }}</label><br v-if="form.studentNo"/>
            <label>注册时间：{{ form.createTime }}</label><br/>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="17">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>个人资料修改</span>
          </div>
          <el-form :model="form" ref="form" label-width="100px" v-loading="formLoading" :rules="rules">
            <el-form-item label="用户名：">
              <el-input v-model="form.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="真实姓名：" prop="realName" required>
              <el-input v-model="form.realName"></el-input>
            </el-form-item>
            <el-form-item label="学号：" v-if="form.studentNo">
              <el-input v-model="form.studentNo" disabled></el-input>
            </el-form-item>
            <el-form-item label="手机：">
              <el-input v-model="form.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱：">
              <el-input v-model="form.email"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm">更新</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userApi from '@/api/user'

export default {
  data () {
    return {
      form: {
        username: '',
        realName: '',
        role: null,
        studentNo: '',
        phone: '',
        email: '',
        avatar: null,
        createTime: null
      },
      formLoading: false,
      uploadHeaders: {},
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    let _this = this
    const token = localStorage.getItem('exam_token')
    if (token) {
      _this.uploadHeaders = { Authorization: 'Bearer ' + token }
    }
    userApi.getCurrentUser().then(re => {
      _this.form = re.data
    })
  },
  methods: {
    roleText (role) {
      let map = { 0: '学生', 1: '教师', 2: '管理员' }
      return map[role] || '未知'
    },
    uploadSuccess (re, file) {
      if (re.code === 200) {
        this.$message.success('头像更新成功')
        window.location.reload()
      } else {
        this.$message.error(re.msg || '上传失败')
      }
    },
    submitForm () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.formLoading = true
          userApi.update({
            realName: _this.form.realName,
            phone: _this.form.phone,
            email: _this.form.email
          }).then(data => {
            if (data.code === 200) {
              _this.$message.success('更新成功')
            }
            _this.formLoading = false
          }).catch(() => {
            _this.formLoading = false
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-card {
  margin-bottom: 20px;
}

.user-info-userName label {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand-dark);
}

.user-info-fullInfo {
  line-height: 2.2;
  font-size: 14px;

  label {
    color: var(--brand-muted);
  }
}
</style>
