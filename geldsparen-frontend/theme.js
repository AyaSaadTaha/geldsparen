// src/theme.js
import { createTheme } from "@mui/material/styles";

const theme = createTheme({
    components: {
        MuiContainer: {
            defaultProps: {
                maxWidth: false,      // remove max-width
                disableGutters: true, //remove paddings
            },
        },
        MuiToolbar: {
            styleOverrides: {
                root: {
                    paddingLeft: 0,
                    paddingRight: 0,
                },
            },
        },
        MuiCssBaseline: {
            styleOverrides: {
                "html, body, #root": { height: "100%" },
                body: { margin: 0 },
                "*": { boxSizing: "border-box" },
            },
        },
    },
});

export default theme;
