module.exports = {
  content: [
    './src/**/*.{html,ts}',
  ],
  important: true,
  prefix: 'tw-',
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/aspect-ratio'),
  ],
}
