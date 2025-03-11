import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import reactX from "eslint-plugin-react-x";
import reactDom from "eslint-plugin-react-dom";
import reactNamingConvention from "eslint-plugin-react-naming-convention";

export default tseslint.config(
  { ignores: ["dist"] },
  // Default TypeScript config
  {
    files: ["**/*.{ts,tsx}"],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parser: tseslint.parser,
      parserOptions: {
        project: ["./tsconfig.node.json", "./tsconfig.app.json"],
        tsconfigRootDir: import.meta.dirname,
      },
    },
    plugins: {
      "@typescript-eslint": tseslint,
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
      "react-x": reactX,
      "react-dom": reactDom,
      "react-naming-convention": reactNamingConvention,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...tseslint.configs.recommendedTypeChecked.rules,
      ...reactHooks.configs.recommended.rules,
      ...reactX.configs["recommended-type-checked"].rules,
      ...reactDom.configs.recommended.rules,
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],

      // Other naming convention rules
      "react-naming-convention/filename-extension": [
        "error",
        {
          allow: "as-needed",
          extensions: [".tsx"],
          ignoreFilesWithoutCode: true,
        },
      ],
      "react-naming-convention/component-name": [
        "error",
        { rule: "PascalCase", allowAllCaps: true },
      ],
      "react-naming-convention/context-name": "error",
      "react-naming-convention/use-state": "error",
    },
  },

  // Component files - PascalCase
  {
    files: ["src/components/**/*.tsx", "src/pages/**/*.tsx"],
    ignore: ["**/index.tsx"],
    rules: {
      "react-naming-convention/filename": ["error", "PascalCase"],
    },
  },
  // // Config and type definition files - kebab-case
  // {
  //   files: ["**/*.config.ts", "**/*.d.ts"],
  //   rules: {
  //     "react-naming-convention/filename": ["error", "kebab-case"],
  //   },
  // },
  // // Hook files - camelCase
  // {
  //   files: ["src/hooks/**/use*.ts", "src/hooks/**/use*.tsx"],
  //   rules: {
  //     "react-naming-convention/filename": ["error", "camelCase"],
  //   },
  // },
  // Turn off filename convention for certain files
  {
    files: ["**/main.tsx", "**/vite-env.d.ts", "**/vite.config.ts"],
    rules: {
      "react-naming-convention/filename": "off",
    },
  }
);
