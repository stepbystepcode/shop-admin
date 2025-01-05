<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :router="true"
        :default-active="route.path"
        class="menu"
      >
        <el-menu-item index="/">
          <el-icon><DataLine /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/merchants">
          <el-icon><Shop /></el-icon>
          <span>Merchants</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>Admin System</h2>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              {{ userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { DataLine, Shop, ArrowDown } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout();
    router.push('/login');
  }
};
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.menu {
  height: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>