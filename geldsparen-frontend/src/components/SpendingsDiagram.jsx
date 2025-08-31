// SpendingPieChart.jsx
import React from 'react';
import {
    Box, Card, CardHeader, CardContent, Checkbox, FormControlLabel,
    List, ListItem, ListItemIcon, ListItemText, Chip, Divider,
    Typography, Stack, NoSsr
} from '@mui/material';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

// legend card
const LegendCard = ({ data, visible, onToggle, totalVisible }) => (
    <Card elevation={0} sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider', width: 320, flex: '0 0 320px' }}>
        <CardHeader
            title={<Typography variant="subtitle1" fontWeight={700}>Categories</Typography>}
            subheader={<Typography variant="caption" color="text.secondary">
                {totalVisible > 0 ? `€${totalVisible.toFixed(2)} total` : 'No data'}
            </Typography>}
            sx={{ pb: 0 }}
        />
        <CardContent sx={{ pt: 1 }}>
            <List dense sx={{ maxHeight: 300, overflow: 'auto', pr: 0.5 }}>
                {data.map((entry) => {
                    const disabled = entry.value <= 0;
                    const isVisible = !!visible[entry.name];
                    const pct = totalVisible > 0 && isVisible ? ((entry.value / totalVisible) * 100).toFixed(1) : '0.0';
                    return (
                        <React.Fragment key={entry.name}>
                            <ListItem
                                disableGutters
                                secondaryAction={<Chip size="small" label={`${pct}%`} sx={{ bgcolor: 'action.hover' }} />}
                                sx={{ px: 1, borderRadius: 2, '&:hover': { bgcolor: 'action.hover' }, opacity: disabled ? 0.5 : 1 }}
                            >
                                <ListItemIcon sx={{ minWidth: 32 }}>
                                    <FormControlLabel
                                        control={
                                            <Checkbox
                                                size="small"
                                                checked={isVisible}
                                                onChange={() => !disabled && onToggle(entry.name)}
                                                disabled={disabled}
                                            />
                                        }
                                        label=""
                                    />
                                </ListItemIcon>
                                <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: entry.color, mr: 1 }} />
                                <ListItemText
                                    primary={<Typography variant="body2" fontWeight={600}>{entry.name}</Typography>}
                                    secondary={<Typography variant="caption" color="text.secondary">€{entry.value.toFixed(2)} • {pct}%</Typography>}
                                />
                            </ListItem>
                            <Divider component="li" sx={{ my: 0.5 }} />
                        </React.Fragment>
                    );
                })}
            </List>
        </CardContent>
    </Card>
);

const SpendingsDiagram = ({ spendingPattern, legendPosition = 'right', title = 'Spending Overview' }) => {
    const [visibleCategories, setVisibleCategories] = React.useState({
        Shopping: true, Rent: true, Bills: true, Food: true
    });

    const toNum = (v) => parseFloat((v ?? 0).toString ? (v ?? 0).toString() : v) || 0;

    const allData = [
        { name: 'Shopping', value: toNum(spendingPattern?.clothes) + toNum(spendingPattern?.miscellaneous), color: '#8B5CF6' },
        { name: 'Rent',     value: toNum(spendingPattern?.renter), color: '#10B981' },
        { name: 'Bills',    value: toNum(spendingPattern?.miscellaneous) * 0.3, color: '#F59E0B' },
        { name: 'Food',     value: toNum(spendingPattern?.food), color: '#EF4444' }
    ];

    const chartData = allData.filter(d => d.value > 0 && visibleCategories[d.name]);
    const totalVisible = chartData.reduce((s, d) => s + d.value, 0);

    const handleCategoryToggle = (name) =>
        setVisibleCategories((p) => ({ ...p, [name]: !p[name] }));

    const sideFirst = legendPosition === 'left';

    return (
        <Card elevation={0} sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider', margin:'15px 125px'}}>
            <CardHeader title={<Typography variant="h6" fontWeight={800}>{title}</Typography>} />
            <CardContent>
                <Stack
                    direction={legendPosition === 'bottom' ? 'column' : 'row'}
                    spacing={1}
                    alignItems="center"
                >
                    {/* legend left */}
                    {sideFirst && (
                        <LegendCard
                            data={allData}
                            visible={visibleCategories}
                            onToggle={handleCategoryToggle}
                            totalVisible={totalVisible}
                        />
                    )}

                    {/* chart */}
                    <Box sx={{ flex: 1, minWidth: 0 }}>
                        <Box sx={{ height: 500 }}>
                            <NoSsr>
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={chartData}
                                            cx="50%"
                                            cy="50%"
                                            innerRadius={0}
                                            outerRadius={180}
                                            paddingAngle={1}
                                            dataKey="value"
                                            isAnimationActive
                                        >
                                            {chartData.map((entry) => (
                                                <Cell key={entry.name} fill={entry.color} />
                                            ))}
                                        </Pie>
                                    </PieChart>
                                </ResponsiveContainer>
                            </NoSsr>
                        </Box>
                    </Box>

                    {/* легенда справа/снизу */}
                    {!sideFirst && legendPosition !== 'bottom' && (
                        <LegendCard
                            data={allData}
                            visible={visibleCategories}
                            onToggle={handleCategoryToggle}
                            totalVisible={totalVisible}
                        />
                    )}

                    {legendPosition === 'bottom' && (
                        <LegendCard
                            data={allData}
                            visible={visibleCategories}
                            onToggle={handleCategoryToggle}
                            totalVisible={totalVisible}
                        />
                    )}
                </Stack>
            </CardContent>
        </Card>
    );
};

export default SpendingsDiagram;
