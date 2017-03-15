<template></template>
<div>
  ahiahiafadf
</div>
<style></style>

<script>
  import {mapGetters, mapState} from 'vuex'
  export default {
    name: 'banker',
    data () {
      return {
      }
    },
    methods: {
      calculate (Data) {
          this.$store.dispatch('calculate', Data)
      }
    }
  }
</script>
